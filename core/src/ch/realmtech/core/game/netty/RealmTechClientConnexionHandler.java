package ch.realmtech.core.game.netty;

import ch.realmtech.core.RealmTech;
import ch.realmtech.server.ServerContext;
import ch.realmtech.server.netty.ConnexionConfig;
import ch.realmtech.server.packet.ServerPacket;
import ch.realmtech.server.packet.clientPacket.ClientExecute;

import java.io.Closeable;
import java.io.IOException;

public class RealmTechClientConnexionHandler implements Closeable {
    private final RealmTech context;
    private ServerContext server;
    private RealmTechClient client;

    public RealmTechClientConnexionHandler(ConnexionConfig connexionConfig, ClientExecute clientExecute, boolean ouvrirServeur, RealmTech context) throws Exception {
        this.context = context;
        if (!ouvrirServeur) {
            try {
                client = new RealmTechClient(connexionConfig, clientExecute);
            } catch (Exception e) {
                close();
                throw e;
            }
        } else {
            try {
                server = new ServerContext(connexionConfig);
                client = new RealmTechClient(connexionConfig, clientExecute);
            } catch (Exception e) {
                close();
                throw e;
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
