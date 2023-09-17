package ch.realmtechServer;

import ch.realmtechCommuns.packet.PacketMap;
import ch.realmtechCommuns.packet.clientPacket.ConnectionAutreJoueurPacket;
import ch.realmtechCommuns.packet.clientPacket.ConnectionJoueurReussitPacket;
import ch.realmtechCommuns.packet.serverPacket.DemandeDeConnectionJoueurPacket;
import ch.realmtechCommuns.packet.serverPacket.ServerExecute;
import ch.realmtechServer.ecs.EcsEngineServer;
import ch.realmtechServer.netty.ConnectionBuilder;
import ch.realmtechServer.netty.ConnectionCommand;
import ch.realmtechServer.netty.ServerExecuteContext;
import ch.realmtechServer.netty.ServerNetty;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

public class ServerContext {
    private final static Logger logger = LoggerFactory.getLogger(ServerContext.class);
    public final static PacketMap PACKETS = new PacketMap();
    private final ServerNetty serverNetty;
    private final EcsEngineServer ecsEngineServer;
    private final ServerExecute serverExecuteContext;

    static {
        PACKETS.put(ConnectionJoueurReussitPacket.class, ConnectionJoueurReussitPacket::new)
                .put(DemandeDeConnectionJoueurPacket.class, DemandeDeConnectionJoueurPacket::new)
                .put(ConnectionAutreJoueurPacket.class, ConnectionAutreJoueurPacket::new)
        ;
    }

    public ServerContext(ConnectionBuilder connectionBuilder) throws Exception {
        ecsEngineServer = new EcsEngineServer();
        ecsEngineServer.prepareSaveToLoad(connectionBuilder.getSaveName());
        serverExecuteContext = new ServerExecuteContext(this);
        serverNetty = new ServerNetty(connectionBuilder, serverExecuteContext);
    }

    public static void main(String[] args) throws Exception {
        ConnectionCommand connectionCommand = new ConnectionCommand();
        new CommandLine(connectionCommand).parseArgs(args);
        new ServerContext(connectionCommand.call());
    }


    public ChannelFuture close() throws InterruptedException {
        return serverNetty.close();
    }


    public static ConnectionBuilder builder() {
        return new ConnectionBuilder();
    }

    public ServerNetty getServerNetty() {
        return serverNetty;
    }

    public EcsEngineServer getEcsEngineServer() {
        return ecsEngineServer;
    }
}
