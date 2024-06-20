package ch.realmtech.server;

import ch.realmtech.server.auth.AuthRequest;
import ch.realmtech.server.cli.CommandServerThread;
import ch.realmtech.server.datactrl.DataCtrl;
import ch.realmtech.server.ecs.Context;
import ch.realmtech.server.ecs.EcsEngineServer;
import ch.realmtech.server.ecs.ExecuteOnContext;
import ch.realmtech.server.ecs.plugin.server.ExecuteOnContextServer;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.mod.InternalConnexion;
import ch.realmtech.server.mod.ModLoader;
import ch.realmtech.server.mod.ModLoaderFail;
import ch.realmtech.server.mod.options.OptionLoader;
import ch.realmtech.server.mod.options.server.VerifyTokenOptionEntry;
import ch.realmtech.server.netty.*;
import ch.realmtech.server.packet.PacketMap;
import ch.realmtech.server.packet.ServerConnexion;
import ch.realmtech.server.packet.serverPacket.ServerExecute;
import ch.realmtech.server.registry.Entry;
import ch.realmtech.server.registry.Registry;
import ch.realmtech.server.registry.RegistryUtils;
import ch.realmtech.server.serialize.SerializerController;
import ch.realmtech.server.tick.TickThread;
import com.badlogic.gdx.utils.Null;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.Closeable;
import java.io.IOException;
import java.util.Optional;

public class ServerContext implements Closeable, Context {
    private final static Logger logger = LoggerFactory.getLogger(ServerContext.class);
    public final static PacketMap PACKETS = new PacketMap();
    public final static String REALMTECH_VERSION = "0.2.1-dev";
    @Null
    private final ServerNetty serverNetty;
    private final EcsEngineServer ecsEngineServer;
    private final ServerExecute serverExecuteContext;
    private final ServerConnexion serverConnexion;
    private final TickThread tickThread;
    private final CommandServerThread commandServerThread;
    private final AuthRequest authRequest;
    private volatile boolean saveAndClose = false;
    private final Registry<?> rootRegistry;
    private InternalConnexion clientRef;
    private final ExecuteOnContextServer executeOnContextServer;
    private final OptionLoader optionLoader;

    public ServerContext(ConnexionConfig connexionConfig) throws Exception {
        try {
            try {
                DataCtrl.creerHiearchieRealmTechData(connexionConfig.getRootPath());
            } catch (IOException e) {
                logger.error("Can not create file structure", e);
                System.exit(1);
            }
            executeOnContextServer = new ExecuteOnContextServer(this);

            if (connexionConfig.getRootRegistry() != null) {
                rootRegistry = connexionConfig.getRootRegistry();
            } else {
                rootRegistry = Registry.createRoot();
                ModLoader modLoader = new ModLoader(this, rootRegistry, null);
                try {
                    modLoader.initializeCoreMod();
                } catch (ModLoaderFail e) {
                    System.exit(1);
                }
            }


            optionLoader = RegistryUtils.evaluateSafe(rootRegistry, OptionLoader.class);
            VerifyTokenOptionEntry verifyTokenOptionEntry = RegistryUtils.findEntryOrThrow(rootRegistry, VerifyTokenOptionEntry.class);
            if (connexionConfig.isNotVerifyAccessToken() != null) {
                verifyTokenOptionEntry.setValue(!connexionConfig.isNotVerifyAccessToken());
                logger.info("Verify access token is defined in the args: {}", verifyTokenOptionEntry.getValue());
            } else {
                logger.info("Verify access token is not defined in the args. Getting saved value in options file: {}", verifyTokenOptionEntry.getValue());
            }

            ecsEngineServer = new EcsEngineServer(this);
            serverExecuteContext = new ServerExecuteContext(this);
            if (connexionConfig.getClientExecute() == null) {
                logger.info("Open a external server");
                serverNetty = new ServerNetty(connexionConfig, serverExecuteContext);
                serverConnexion = new ServerConnexionExtern(this);
            } else {
                logger.info("Open a internal server");
                serverConnexion = new ServerConnexionIntern(this, connexionConfig.getClientExecute());
                serverNetty = null;
            }
            commandServerThread = new CommandServerThread(this);
            tickThread = new TickThread(this);
            ecsEngineServer.prepareSaveToLoad(connexionConfig);
            authRequest = new AuthRequest(this);

            tickThread.start();
            commandServerThread.start();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            try {
                saveAndClose();
            } catch (Exception ignored) {}
            throw e;
        }
    }

    public static void main(String[] args) throws Exception {
        ConnexionCommand connexionCommand = new ConnexionCommand();
        CommandLine commandLine = new CommandLine(connexionCommand);
        try {
            commandLine.parseArgs(args);
        } catch (Exception e) {
            commandLine.usage(System.out);
            System.exit(0);
            return;
        }
        ConnexionConfig connexionConfig = connexionCommand.call();
        if (commandLine.isUsageHelpRequested()) {
            commandLine.usage(System.out);
            return;
        }
        if (commandLine.isVersionHelpRequested()) {
            commandLine.printVersionHelp(System.out);
            return;
        }
        ServerContext serverContext = new ServerContext(connexionConfig);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (!serverContext.saveAndClose) {
                try {
                    serverContext.saveAndClose();
                } catch (Exception e) {
                    System.err.println("Can not close server on shutdownHook. Error: " + e.getMessage());
                    System.err.println(e);
                }
            }
        }));
    }

    public void saveAndClose() throws InterruptedException, IOException {
        if (saveAndClose) {
            // already server closed
            return;
        }
        try {
            save();
        } catch (IOException e) {
            logger.error("Can not save {}", e.getMessage(), e);
        }
        try {
            close();
        } catch (Exception e) {
            logger.error("Can not close server {}", e.getMessage(), e);
        }
    }

    @Override
    public void close() throws IOException {
        synchronized (this) {
            logger.info("Closing server...");
            tickThread.close();
            commandServerThread.close();
            ecsEngineServer.dispose();
            saveAndClose = true;
            try {
                if (serverNetty != null) {
                    serverNetty.close().await();
                }
            } catch (InterruptedException e) {
                throw new IOException(e);
            }
            if (clientRef != null) {
                clientRef.closeEcs();
                clientRef.setMenuScreen();
            }
        }
    }

    public void save() throws IOException {
        logger.info("Saving map...");
        optionLoader.saveServerOptions();
        ecsEngineServer.saveMap();
        getSystemsAdminServer().getPlayerManagerServer().savePlayers();
    }

    public ServerNetty getServerNetty() {
        return serverNetty;
    }

    public EcsEngineServer getEcsEngineServer() {
        return ecsEngineServer;
    }

    public ServerConnexion getServerConnexion() {
        return serverConnexion;
    }

    public SystemsAdminServer getSystemsAdminServer() {
        return ecsEngineServer.getSystemsAdminServer();
    }

    public SerializerController getSerializerController() {
        return ecsEngineServer.getSerializerController();
    }

    public AuthRequest getAuthController() {
        return authRequest;
    }

    public ServerExecute getServerExecuteContext() {
        return serverExecuteContext;
    }

    public Registry<?> getRootRegistry() {
        return rootRegistry;
    }

    public void setClientRef(InternalConnexion clientRef) {
        this.clientRef = clientRef;
    }

    public Optional<InternalConnexion> getClientInternalConnexion() {
        return Optional.ofNullable(clientRef);
    }

    public ExecuteOnContextServer getExecuteOnContextServer() {
        return executeOnContextServer;
    }

    public boolean isInternalServer() {
        return getClientInternalConnexion().isPresent();
    }

    @Override
    public ExecuteOnContext getExecuteOnContext() {
        return executeOnContextServer;
    }

    @Override
    public Entry getDefaultSystemAdminClientEntry() {
        return null;
    }
}
