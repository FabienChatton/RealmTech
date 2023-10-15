package ch.realmtech.game.netty;

import ch.realmtech.RealmTech;
import ch.realmtechServer.ServerContext;
import ch.realmtechServer.netty.ConnexionBuilder;
import ch.realmtechServer.netty.ServerNetty;
import ch.realmtechServer.packet.ServerPacket;
import ch.realmtechServer.packet.clientPacket.ClientExecute;

import java.io.Closeable;
import java.io.IOException;
import java.util.Random;

public class RealmTechClientConnexionHandler implements Closeable {
    private final RealmTech context;
    private ServerContext server;
    private final RealmTechClient client;

    public RealmTechClientConnexionHandler(ConnexionBuilder connexionBuilder, ClientExecute clientExecute, boolean ouvrirServeur, RealmTech context) throws Exception {
        this.context = context;
        if (!ouvrirServeur) {
            try {
                client = new RealmTechClient(connexionBuilder, clientExecute);
            } catch (Exception e) {
                close();
                throw e;
            }
        } else {
            try {
                if (!ServerNetty.isPortAvailable(connexionBuilder.getPort())) {
                    byte limite = 0;
                    do {
                        connexionBuilder.setPort(new Random().nextInt(1024, 65565));
                    } while (!ServerNetty.isPortAvailable(connexionBuilder.getPort()) || ++limite < 10);
                }
                server = new ServerContext(connexionBuilder.setSaveName("default"));
                client = new RealmTechClient(connexionBuilder, clientExecute);
            } catch (Exception e) {
                throw new IOException(e);
            }
        }
    }

    @Override
    public void close() throws IOException {
        try {
            if (client != null) client.close();
            if (server != null) server.close().await();
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
    }

    public void sendAndFlushPacketToServer(ServerPacket packet) {
        if (context.getEcsEngine() != null) {
            context.getEcsEngine().serverTickBeatMonitoring.addPacketSend(packet);
        }
        client.getChannel().writeAndFlush(packet);
    }
}
