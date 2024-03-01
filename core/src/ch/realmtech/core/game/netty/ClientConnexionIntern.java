package ch.realmtech.core.game.netty;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.netty.ConnexionConfig;
import ch.realmtech.server.packet.ServerPacket;
import io.netty.channel.Channel;
import io.netty.channel.embedded.EmbeddedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ClientConnexionIntern implements ClientConnexion {
    private final static Logger logger = LoggerFactory.getLogger(ClientConnexionExtern.class);
    private final Channel clientChanel;
    private final ServerContext serverContext;

    public ClientConnexionIntern(ConnexionConfig connexionConfig) throws Exception {
        this.clientChanel = new EmbeddedChannel();
        try {
            serverContext = new ServerContext(connexionConfig);
        } catch (Exception e) {
            logger.error("Can not open server");
            throw e;
        }
    }

    @Override
    public void sendAndFlushPacketToServer(ServerPacket serverPacket) {
        serverPacket.executeOnServer(clientChanel, serverContext.getServerExecuteContext());
    }

    @Override
    public void close() throws IOException {
        try {
            serverContext.saveAndClose();
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
    }
}
