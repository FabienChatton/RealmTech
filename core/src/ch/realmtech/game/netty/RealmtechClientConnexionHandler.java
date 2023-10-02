package ch.realmtech.game.netty;

import ch.realmtechServer.ServerContext;
import ch.realmtechServer.netty.ConnexionBuilder;
import ch.realmtechServer.netty.ServerNetty;
import ch.realmtechServer.packet.ServerPacket;
import ch.realmtechServer.packet.clientPacket.ClientExecute;

import java.io.Closeable;
import java.io.IOException;
import java.util.Random;

public class RealmtechClientConnexionHandler implements Closeable {
    private ServerContext server;
    private final RealmtechClient client;


    public RealmtechClientConnexionHandler(ConnexionBuilder connexionBuilder, ClientExecute clientExecute, boolean ouvrirServeur) throws Exception {
        if (!ouvrirServeur) {
            try {
                client = new RealmtechClient(connexionBuilder, clientExecute);
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
                client = new RealmtechClient(connexionBuilder, clientExecute);
            } catch (Exception e) {
                throw new IOException(e);
            }
        }
    }

    @Override
    public void close() throws IOException {
        try {
            if (client != null) client.close();
            if (server != null) server.close();
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
    }

    public void sendAndFlushPacketToServer(ServerPacket packet) {
        client.getChannel().writeAndFlush(packet);
    }
}
