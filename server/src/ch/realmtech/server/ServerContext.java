package ch.realmtech.server;

import ch.realmtech.server.cli.CommandServerThread;
import ch.realmtech.server.cli.CommandeServerExecute;
import ch.realmtech.server.ecs.EcsEngineServer;
import ch.realmtech.server.netty.*;
import ch.realmtech.server.packet.PacketMap;
import ch.realmtech.server.packet.ServerResponseHandler;
import ch.realmtech.server.packet.clientPacket.*;
import ch.realmtech.server.packet.serverPacket.*;
import ch.realmtech.server.tick.TickThread;
import com.artemis.BaseSystem;
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
    private final CommandServerThread commandServerThread;
    private final CommandeServerExecute commandeServerExecute;

    static {
        PACKETS.put(ConnexionJoueurReussitPacket.class, ConnexionJoueurReussitPacket::new)
                .put(DemandeDeConnexionJoueurPacket.class, DemandeDeConnexionJoueurPacket::new)
                .put(TousLesJoueurPacket.class, TousLesJoueurPacket::new)
                .put(PlayerMovePacket.class, PlayerMovePacket::new)
                .put(ChunkAMonterPacket.class, ChunkAMonterPacket::new)
                .put(ChunkADamnePacket.class, ChunkADamnePacket::new)
                .put(ChunkAReplacePacket.class, ChunkAReplacePacket::new)
                .put(DeconnectionJoueurPacket.class, DeconnectionJoueurPacket::new)
                .put(CellBreakRequestPacket.class, CellBreakRequestPacket::new)
                .put(CellBreakPacket.class, CellBreakPacket::new)
                .put(TickBeatPacket.class, TickBeatPacket::new)
                .put(GetPlayerInventorySessionPacket.class, GetPlayerInventorySessionPacket::new)
                .put(PlayerInventoryPacket.class, PlayerInventoryPacket::new)
                .put(ItemOnGroundPacket.class, ItemOnGroundPacket::new)
                .put(ItemOnGroundSupprimerPacket.class, ItemOnGroundSupprimerPacket::new)
                .put(ConsoleCommandeRequestPacket.class, ConsoleCommandeRequestPacket::new)
                .put(WriteToConsolePacket.class, WriteToConsolePacket::new)
                .put(PlayerInventorySetRequestPacket.class, PlayerInventorySetRequestPacket::new)
                .put(InventoryMoveItemsRequest.class, InventoryMoveItemsRequest::new)
                .put(InventorySetPacket.class, InventorySetPacket::new)
        ;
    }

    public ServerContext(ConnexionBuilder connexionBuilder) throws Exception {
        try {
            ecsEngineServer = new EcsEngineServer(this);
            ecsEngineServer.prepareSaveToLoad(connexionBuilder.getSaveName());
            serverExecuteContext = new ServerExecuteContext(this);
            serverNetty = new ServerNetty(connexionBuilder, serverExecuteContext);
            serverResponseHandler = new ServerResponse();
            commandeServerExecute = new CommandeServerExecute(this);
            commandServerThread = new CommandServerThread(this, commandeServerExecute);
            tickThread = new TickThread(this);
            commandServerThread.start();
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
        commandServerThread.close();
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

    public <T extends BaseSystem> T getSystem(Class<T> systemClass) {
        return ecsEngineServer.getWorld().getSystem(systemClass);
    }

    public CommandeServerExecute getCommandeExecute() {
        return commandeServerExecute;
    }
}