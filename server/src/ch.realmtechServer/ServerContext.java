package ch.realmtechServer;

import ch.realmtechCommuns.packet.PacketMap;
import ch.realmtechCommuns.packet.ServerResponseHandler;
import ch.realmtechCommuns.packet.clientPacket.ConnectionJoueurReussitPacket;
import ch.realmtechCommuns.packet.clientPacket.TousLesJoueurPacket;
import ch.realmtechCommuns.packet.serverPacket.DemandeDeConnectionJoueurPacket;
import ch.realmtechCommuns.packet.serverPacket.PlayerMovePacket;
import ch.realmtechCommuns.packet.serverPacket.ServerExecute;
import ch.realmtechServer.ecs.EcsEngineServer;
import ch.realmtechServer.netty.*;
import ch.realmtechServer.tick.TickThread;
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
    private final ServerResponseHandler serverResponseHandler;
    private final TickThread tickThread;

    static {
        PACKETS.put(ConnectionJoueurReussitPacket.class, ConnectionJoueurReussitPacket::new)
                .put(DemandeDeConnectionJoueurPacket.class, DemandeDeConnectionJoueurPacket::new)
                .put(TousLesJoueurPacket.class, TousLesJoueurPacket::new)
                .put(PlayerMovePacket.class, PlayerMovePacket::new)
        ;
    }

    public ServerContext(ConnectionBuilder connectionBuilder) throws Exception {
        ecsEngineServer = new EcsEngineServer(this);
        ecsEngineServer.prepareSaveToLoad(connectionBuilder.getSaveName());
        serverExecuteContext = new ServerExecuteContext(this);
        serverNetty = new ServerNetty(connectionBuilder, serverExecuteContext);
        serverResponseHandler = new ServerResponse();
        tickThread = new TickThread(this);
        tickThread.start();
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

    public ServerResponseHandler getServerHandler() {
        return serverResponseHandler;
    }
}
