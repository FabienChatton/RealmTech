package ch.realmtech.game.netty;

import ch.realmtechCommuns.packet.ServerPacket;
import ch.realmtechCommuns.packet.clientPacket.ClientExecute;
import ch.realmtechServer.ServerContext;
import ch.realmtechServer.netty.ConnexionBuilder;
import ch.realmtechServer.netty.ServerNetty;

import java.io.Closeable;
import java.io.IOException;
import java.util.Random;

public class RealmtechClientConnexionHandler implements Closeable {
    private ServerContext server;
    private final RealmtechClient client;

    /**
     * Pour une connexion en multi
     */
    public RealmtechClientConnexionHandler(ConnexionBuilder connexionBuilder, ClientExecute clientExecute) throws IOException {
        try {
            client = new RealmtechClient(connexionBuilder, clientExecute);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    /**
     * Destiner pour une connexion en solo
     */
    public RealmtechClientConnexionHandler(ClientExecute clientExecute) throws IOException {
        try {
            ConnexionBuilder connexionBuilder = ServerContext.builder();
            if (!ServerNetty.isPortAvailable(connexionBuilder.getPort())) {
                byte limite = 0;
                do {
                    connexionBuilder.setPort(new Random().nextInt(1024, 65565));
                } while (!ServerNetty.isPortAvailable(connexionBuilder.getPort()) || ++limite < 10);
            }
            server = new ServerContext(connexionBuilder);
            client = new RealmtechClient(connexionBuilder, clientExecute);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            client.close();
            if (server != null) server.close();
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
    }

    public void sendAndFlushPacketToServer(ServerPacket packet) {
        client.getChannel().writeAndFlush(packet);
    }
}
