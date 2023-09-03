package ch.realmtech.game.netty;

import ch.realmtechServer.netty.ConnectionBuilder;
import ch.realmtechServer.netty.RealmTechServer;

import java.io.Closeable;
import java.io.IOException;
import java.util.Random;

public class RealmtechClientConnectionHandler implements Closeable {
    private final RealmTechServer server;
    private final RealmtechClient client;

    public RealmtechClientConnectionHandler() throws IOException {
        try {
            ConnectionBuilder connectionBuilder = RealmTechServer.builder();
            if (!RealmTechServer.isPortAvailable(connectionBuilder.getPort())) {
                byte limite = 0;
                do {
                    connectionBuilder.setPort(new Random().nextInt(1024, 65565));
                } while (!RealmTechServer.isPortAvailable(connectionBuilder.getPort()) || ++limite < 10);
            }
            server = new RealmTechServer(connectionBuilder);
            client = new RealmtechClient(connectionBuilder);
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
