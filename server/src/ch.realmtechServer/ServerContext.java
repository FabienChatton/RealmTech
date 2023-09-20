package ch.realmtechServer;

import ch.realmtechCommuns.packet.PacketMap;
import ch.realmtechCommuns.packet.ServerResponseHandler;
import ch.realmtechCommuns.packet.clientPacket.ConnectionJoueurReussitPacket;
import ch.realmtechCommuns.packet.clientPacket.TousLesJoueurPacket;
import ch.realmtechCommuns.packet.serverPacket.DemandeDeConnectionJoueurPacket;
import ch.realmtechCommuns.packet.serverPacket.PlayerMovePacket;
import ch.realmtechCommuns.packet.serverPacket.ServerExecute;
import ch.realmtechServer.cli.CommandThread;
import ch.realmtechServer.ecs.EcsEngineServer;
import ch.realmtechServer.netty.*;
import ch.realmtechServer.tick.TickThread;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.IOException;

public class ServerContext {
    private final static Logger logger = LoggerFactory.getLogger(ServerContext.class);
    public final static PacketMap PACKETS = new PacketMap();
    private final ServerNetty serverNetty;
    private final EcsEngineServer ecsEngineServer;
    private final ServerExecute serverExecuteContext;
    private final ServerResponseHandler serverResponseHandler;
    private final TickThread tickThread;
    private final CommandThread commandThread;

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
        commandThread = new CommandThread(this);
        tickThread = new TickThread(this);
        commandThread.start();
        tickThread.start();
    }

    public static void main(String[] args) throws Exception {
        ConnectionCommand connectionCommand = new ConnectionCommand();
        CommandLine commandLine = new CommandLine(connectionCommand);
        commandLine.parseArgs(args);
        ConnectionBuilder connectionBuilder = connectionCommand.call();
        if (commandLine.isUsageHelpRequested()) {
            commandLine.usage(System.out);
            return;
        }
        if (commandLine.isVersionHelpRequested()) {
            commandLine.printVersionHelp(System.out);
            return;
        }
        new ServerContext(connectionBuilder);
    }


    public ChannelFuture close() throws InterruptedException, IOException {
        logger.info("Fermeture du serveur...");
        tickThread.close();
        commandThread.close();
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
