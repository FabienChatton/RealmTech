package ch.realmtechServer;

import ch.realmtechCommuns.packet.PacketMap;
import ch.realmtechCommuns.packet.ServerResponseHandler;
import ch.realmtechCommuns.packet.clientPacket.*;
import ch.realmtechCommuns.packet.serverPacket.DemandeDeConnexionJoueurPacket;
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
        PACKETS.put(ConnexionJoueurReussitPacket.class, ConnexionJoueurReussitPacket::new)
                .put(DemandeDeConnexionJoueurPacket.class, DemandeDeConnexionJoueurPacket::new)
                .put(TousLesJoueurPacket.class, TousLesJoueurPacket::new)
                .put(PlayerMovePacket.class, PlayerMovePacket::new)
                .put(ChunkAMonterPacket.class, ChunkAMonterPacket::new)
                .put(ChunkADamnePacket.class, ChunkADamnePacket::new)
                .put(ChunkAReplacePacket.class, ChunkAReplacePacket::new)
        ;
    }

    public ServerContext(ConnexionBuilder connexionBuilder) throws Exception {
        try {
            ecsEngineServer = new EcsEngineServer(this);
            ecsEngineServer.prepareSaveToLoad(connexionBuilder.getSaveName());
            serverExecuteContext = new ServerExecuteContext(this);
            serverNetty = new ServerNetty(connexionBuilder, serverExecuteContext);
            serverResponseHandler = new ServerResponse();
            commandThread = new CommandThread(this);
            tickThread = new TickThread(this);
            commandThread.start();
            tickThread.start();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            try {
                close();
            } catch (Exception ignored) {}
            throw e;
        }
    }

    public static void main(String[] args) throws Exception {
        ConnexionCommand connexionCommand = new ConnexionCommand();
        CommandLine commandLine = new CommandLine(connexionCommand);
        commandLine.parseArgs(args);
        ConnexionBuilder connexionBuilder = connexionCommand.call();
        if (commandLine.isUsageHelpRequested()) {
            commandLine.usage(System.out);
            return;
        }
        if (commandLine.isVersionHelpRequested()) {
            commandLine.printVersionHelp(System.out);
            return;
        }
        new ServerContext(connexionBuilder);
    }


    public ChannelFuture close() throws InterruptedException, IOException {
        logger.info("Fermeture du serveur...");
        try {
            ecsEngineServer.saveMap();
        } catch (IOException ignored) {}
        tickThread.close();
        commandThread.close();
        return serverNetty.close();
    }


    public static ConnexionBuilder builder() {
        return new ConnexionBuilder();
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
    public void nextTick(Runnable runnable) {
        ecsEngineServer.nextTickServer(runnable);
    }
}