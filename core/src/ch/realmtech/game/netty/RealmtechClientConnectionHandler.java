package ch.realmtech.game.netty;

import ch.realmtechServer.netty.RealmTechServer;

import java.io.Closeable;
import java.io.IOException;

public class RealmtechClientConnectionHandler implements Closeable {
    private final RealmTechServer server;
    private final RealmtechClient client;

    public RealmtechClientConnectionHandler() throws IOException {
        try {
            server = new RealmTechServer();
            client = new RealmtechClient();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            client.close();
            server.close();
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
    }
}
