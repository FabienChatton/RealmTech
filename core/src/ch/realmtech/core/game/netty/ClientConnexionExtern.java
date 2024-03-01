package ch.realmtech.core.game.netty;

import ch.realmtech.core.RealmTech;
import ch.realmtech.server.ServerContext;
import ch.realmtech.server.netty.ConnexionConfig;
import ch.realmtech.server.packet.ServerPacket;
import ch.realmtech.server.packet.clientPacket.ClientExecute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ClientConnexionExtern implements ClientConnexion {
    private final static Logger logger = LoggerFactory.getLogger(ClientConnexionExtern.class);
    private final RealmTech context;
    private final ServerContext server;
    private final RealmTechClient client;

    private ClientConnexionExtern(RealmTech context, ServerContext server, RealmTechClient client) {
        this.context = context;
        this.server = server;
        this.client = client;
    }

    public static ClientConnexionExtern create(ConnexionConfig connexionConfig, ClientExecute clientExecute, RealmTech context) throws Exception {
        RealmTechClient client;
        try {
            client = new RealmTechClient(connexionConfig, clientExecute);
        } catch (Exception e) {
            logger.error("Can not open client");
            throw e;
        }
        return new ClientConnexionExtern(context, null, client);
    }

    @Override
    public void close() throws IOException {
        try {
            if (client != null) client.close();
            if (server != null) server.saveAndClose();
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
