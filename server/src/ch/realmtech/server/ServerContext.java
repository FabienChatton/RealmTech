package ch.realmtech.server;

import ch.realmtech.server.auth.AuthRequest;
import ch.realmtech.server.cli.CommandServerThread;
import ch.realmtech.server.cli.CommandeServerExecute;
import ch.realmtech.server.datactrl.OptionServer;
import ch.realmtech.server.ecs.EcsEngineServer;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.ecs.system.PlayerManagerServer;
import ch.realmtech.server.netty.*;
import ch.realmtech.server.packet.PacketMap;
import ch.realmtech.server.packet.ServerResponseHandler;
import ch.realmtech.server.packet.clientPacket.*;
import ch.realmtech.server.packet.serverPacket.*;
import ch.realmtech.server.serialize.SerializerController;
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
    private final AuthRequest authRequest;
    private OptionServer optionServer;

    static {
        PACKETS.put(ConnexionJoueurReussitPacket.class, ConnexionJoueurReussitPacket::new)
                .put(DemandeDeConnexionJoueurPacket.class, DemandeDeConnexionJoueurPacket::new)
                .put(PlayerMovePacket.class, PlayerMovePacket::new)
                .put(ChunkAMonterPacket.class, ChunkAMonterPacket::new)
                .put(ChunkADamnePacket.class, ChunkADamnePacket::new)
                .put(ChunkAReplacePacket.class, ChunkAReplacePacket::new)
                .put(DeconnectionJoueurPacket.class, DeconnectionJoueurPacket::new)
                .put(CellBreakRequestPacket.class, CellBreakRequestPacket::new)
                .put(CellBreakPacket.class, CellBreakPacket::new)
                .put(TickBeatPacket.class, TickBeatPacket::new)
                .put(GetPlayerInventorySessionPacket.class, GetPlayerInventorySessionPacket::new)
                .put(ItemOnGroundPacket.class, ItemOnGroundPacket::new)
                .put(ItemOnGroundSupprimerPacket.class, ItemOnGroundSupprimerPacket::new)
                .put(ConsoleCommandeRequestPacket.class, ConsoleCommandeRequestPacket::new)
                .put(WriteToConsolePacket.class, WriteToConsolePacket::new)
                .put(MoveStackToStackPacket.class, MoveStackToStackPacket::new)
                .put(InventorySetPacket.class, InventorySetPacket::new)
                .put(InventoryGetPacket.class, InventoryGetPacket::new)
                .put(ItemToCellPlaceRequestPacket.class, ItemToCellPlaceRequestPacket::new)
                .put(CellAddPacket.class, CellAddPacket::new)
                .put(DisconnectMessagePacket.class, DisconnectMessagePacket::new)
                .put(TimeGetRequestPacket.class, TimeGetRequestPacket::new)
                .put(TimeSetPacket.class, TimeSetPacket::new)
                .put(PhysicEntitySetPacket.class, PhysicEntitySetPacket::new)
                .put(PlayerSyncPacket.class, PlayerSyncPacket::new)
                .put(PlayerCreateConnexion.class, PlayerCreateConnexion::new)
                .put(FurnaceExtraInfoPacket.class, FurnaceExtraInfoPacket::new)
                .put(RotateFaceCellRequestPacket.class, RotateFaceCellRequestPacket::new)
                .put(CellSetPacket.class, CellSetPacket::new)
                .put(EnergyBatterySetEnergyPacket.class, EnergyBatterySetEnergyPacket::new)
                .put(EnergyGeneratorInfoPacket.class, EnergyGeneratorInfoPacket::new)
        ;
    }

    public ServerContext(ConnexionBuilder connexionBuilder) throws Exception {
        try {
            reloadOption();
            optionServer.verifyAccessToken.set(connexionBuilder.isVerifyAccessToken());
            ecsEngineServer = new EcsEngineServer(this);
            ecsEngineServer.prepareSaveToLoad(connexionBuilder.getSaveName());
            serverExecuteContext = new ServerExecuteContext(this);
            serverNetty = new ServerNetty(connexionBuilder, serverExecuteContext);
            serverResponseHandler = new ServerResponse(this);
            commandeServerExecute = new CommandeServerExecute(this);
            commandServerThread = new CommandServerThread(this, commandeServerExecute);
            tickThread = new TickThread(this);
            commandServerThread.start();
            tickThread.start();
            authRequest = new AuthRequest(this);
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
            optionServer.save();
        } catch (Exception e) {
            logger.error("Option file can not be saved. {}", e.getMessage());
        }
        try {
            ecsEngineServer.saveMap();
        } catch (Exception ignored) {}
        try {
            getSystem(PlayerManagerServer.class).savePlayers();
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

    public SystemsAdminServer getSystemsAdmin() {
        return ecsEngineServer.getSystemsAdminServer();
    }

    public CommandeServerExecute getCommandeExecute() {
        return commandeServerExecute;
    }
    public SerializerController getSerializerController() {
        return ecsEngineServer.getSerializerController();
    }

    public AuthRequest getAuthController() {
        return authRequest;
    }

    public OptionServer getOptionServer() {
        return optionServer;
    }

    public void reloadOption() throws IOException {
        optionServer = OptionServer.getOptionFileAndLoadOrCreate();
    }
}
