package ch.realmtech.core;

import ch.realmtech.core.auth.AuthControllerClient;
import ch.realmtech.core.auth.AuthRequestClient;
import ch.realmtech.core.discord.Discord;
import ch.realmtech.core.game.ecs.ECSEngine;
import ch.realmtech.core.game.ecs.plugin.ExecuteOnContextClient;
import ch.realmtech.core.game.ecs.plugin.SystemsAdminClient;
import ch.realmtech.core.game.ecs.system.PlayerInventorySystem;
import ch.realmtech.core.game.ecs.system.PlayerManagerClient;
import ch.realmtech.core.game.netty.ClientConnexion;
import ch.realmtech.core.game.netty.ClientConnexionExtern;
import ch.realmtech.core.game.netty.ClientConnexionIntern;
import ch.realmtech.core.game.netty.ClientExecuteContext;
import ch.realmtech.core.helper.Popup;
import ch.realmtech.core.input.InputMapper;
import ch.realmtech.core.screen.AbstractScreen;
import ch.realmtech.core.screen.AuthenticateScreen;
import ch.realmtech.core.screen.GameScreen;
import ch.realmtech.core.screen.ScreenType;
import ch.realmtech.server.datactrl.DataCtrl;
import ch.realmtech.server.ecs.ExecuteOnContext;
import ch.realmtech.server.ecs.GetWorld;
import ch.realmtech.server.inventory.AddAndDisplayInventoryArgs;
import ch.realmtech.server.mod.InternalConnexion;
import ch.realmtech.server.mod.ModLoader;
import ch.realmtech.server.mod.options.OptionLoader;
import ch.realmtech.server.mod.options.client.SoundOptionEntry;
import ch.realmtech.server.netty.ConnexionConfig;
import ch.realmtech.server.packet.ServerPacket;
import ch.realmtech.server.packet.clientPacket.ClientExecute;
import ch.realmtech.server.registry.Registry;
import ch.realmtech.server.registry.RegistryUtils;
import ch.realmtech.server.serialize.SerializerController;
import ch.realmtech.server.sound.SoundManager;
import com.artemis.BaseSystem;
import com.artemis.World;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Null;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class RealmTech extends Game implements InternalConnexion {
    public final static float WORLD_WIDTH = 16f;
    public final static float WORLD_HEIGHT = 9f;
    public final static int SCREEN_WIDTH = DataCtrl.SCREEN_WIDTH;
    public final static int SCREEN_HEIGHT = DataCtrl.SCREEN_HEIGHT;
    public final static float PPM = SCREEN_WIDTH / WORLD_WIDTH;
    public final static float UNITE_SCALE = 1 / 32f;
    private final static Logger logger = LoggerFactory.getLogger(RealmTech.class);
    private InputMapper inputMapper;
    private AssetManager assetManager;
    private Stage gameStage;
    private Stage uiStage;
    private Skin skin;
    private volatile ECSEngine ecsEngine;
    private Discord discord;

    private TextureAtlas textureAtlas;
    private SoundManager soundManager;
    private ClientExecute clientExecute;
    private ScreenType currentScreenType;
    private GameScreen gameScreen;
    private AuthRequestClient authRequestClient;
    private AuthControllerClient authControllerClient;
    private boolean verifyAccessToken;
    private List<Consumer<GetWorld>> onEcsEngineInitializes;
    private ExecuteOnContextClient executeOnContextClient;
    private Registry<?> rootRegistry;
    private OptionLoader optionLoader;

    @Override
    public void create() {
        onEcsEngineInitializes = new ArrayList<>();
        try {
            String os = (System.getProperty("os.name")).toUpperCase();
            String rootPath;
            if (os.contains("WIN")) {
                rootPath = System.getenv("AppData");
            } else {
                // linux
                rootPath = System.getProperty("user.home");
            }
            DataCtrl.creerHiearchieRealmTechData(rootPath);
        } catch (IOException e) {
            logger.error("Can not create file structure", e);
            Gdx.app.exit();
        }
        executeOnContextClient = new ExecuteOnContextClient(this);

        rootRegistry = Registry.createRoot();
        ModLoader modLoader = new ModLoader(this, rootRegistry);
        modLoader.initializeCoreMod();

        try {
            optionLoader = RegistryUtils.evaluateSafe(rootRegistry, OptionLoader.class);
        } catch (Exception e) {
            logger.error("Can not create option loader.", e);
            Gdx.app.exit();
        }
        assetManager = new AssetManager();
        initSkin();
        initMap();
        initSound();
        Box2D.init();
        Integer soundOption = RegistryUtils.findEntry(rootRegistry, SoundOptionEntry.class).orElseThrow(() -> new RuntimeException("Can not find sound option")).getValue();
        soundManager = new SoundManager(assetManager, soundOption);
        discord = new Discord(Thread.currentThread());
        discord.init();
        gameStage = new Stage(
                new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT,
                        new OrthographicCamera(WORLD_WIDTH, WORLD_HEIGHT)));
        uiStage = new Stage(
                new ExtendViewport(SCREEN_WIDTH, SCREEN_HEIGHT,
                        new OrthographicCamera(SCREEN_WIDTH, SCREEN_HEIGHT)));

        inputMapper = InputMapper.getInstance(this);
        clientExecute = new ClientExecuteContext(this);
        setScreen(ScreenType.LOADING);
        authRequestClient = new AuthRequestClient(this);
        authControllerClient = new AuthControllerClient(authRequestClient);

    }

    public void loadingFinish() {
        setScreen(ScreenType.AUTHENTICATE);
        ((AuthenticateScreen) getScreen()).hasPersisteCredential();
    }

    @Override
    public void render() {
        screen.render(Gdx.graphics.getDeltaTime());
    }

    private void initSkin() {
		assetManager.load("skin/uiSkinComposer.json", Skin.class);
		assetManager.load("skin/uiSkinComposer.atlas", Skin.class);
		assetManager.load("skin/uiSkinComposer.png", Skin.class);
		assetManager.finishLoading();
		skin = assetManager.get("skin/uiSkinComposer.json", Skin.class);
		logger.trace("skin initialise");
    }

    private void initMap() {
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(assetManager.getFileHandleResolver()));
        assetManager.load("texture/atlas/texture.atlas", TextureAtlas.class);
        logger.trace("atlas chargé");
    }

    private void initSound() {
        SoundManager.LoadAsset(assetManager);
    }

    public void setScreen(final ScreenType screenType) {
        try {
            Screen currentScreen = getScreen();
            AbstractScreen screen = screenType.screenClass.getConstructor(RealmTech.class).newInstance(this);
            screen.setOldScreen(currentScreenType);
            if (screenType == ScreenType.GAME_SCREEN) {
                if (gameScreen == null) {
                    gameScreen = (GameScreen) screen;
                }
                setScreen(gameScreen);
            } else {
                setScreen(screen);
            }
            if (!(currentScreenType == ScreenType.GAME_SCREEN && screenType == ScreenType.GAME_PAUSE)) {
                if (currentScreen != null) {
                    currentScreen.dispose();
                    currentScreen = null;
                }
            }
            currentScreenType = screenType;
        } catch (ReflectiveOperationException e) {
            logger.error("La class {} n'a pas pu etre cree", screenType, e);
        } catch (Exception e) {
            dispose();
            Gdx.app.exit();
        }
    }

    public ScreenType getScreenType() {
        return currentScreenType;
    }

    @Override
    public World getWorld() {
        synchronized (this) {
            return getEcsEngine().getWorld();
        }
    }

    @Override
    public int getPlayerId() {
        return getSystem(PlayerManagerClient.class).getMainPlayer();
    }

    @Override
    public void openPlayerInventory(Supplier<AddAndDisplayInventoryArgs> openPlayerInventorySupplier) {
        getSystem(PlayerInventorySystem.class).openPlayerInventory(openPlayerInventorySupplier);
    }

    public Skin getSkin() {
		return skin;
	}

    @Override
    public void sendRequest(ServerPacket packet) {
        ecsEngine.getClientConnexion().sendAndFlushPacketToServer(packet);
    }

    public Stage getGameStage() {
		return gameStage;
	}

	public Stage getUiStage() {
		return uiStage;
	}

	public ECSEngine getEcsEngine() {
		return ecsEngine;
	}

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public InputMapper getInputManager() {
        return inputMapper;
    }

    @Override
    public void dispose() {
        gameStage.dispose();
        uiStage.dispose();
        assetManager.dispose();
        try {
            optionLoader.saveClientOption();
        } catch (IOException e) {
            logger.error("Option file can not be saved. {}", e.getMessage());
        }
        discord.stop();
        closeEcs();
    }

    public TextureAtlas getTextureAtlas() {
        if (textureAtlas == null) {
            textureAtlas = assetManager.get("texture/atlas/texture.atlas");
        }
        return textureAtlas;
    }

    public void drawGameScreen() {
        if (gameScreen != null) {
            gameScreen.draw();
        }
    }

    public void process(float deltaTime) {
        try {
            if (ecsEngine != null) {
                ecsEngine.process(deltaTime);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            if (uiStage.getBatch().isDrawing()) {
                uiStage.getBatch().end();
            }
            if (gameStage.getBatch().isDrawing()) {
                gameStage.getBatch().end();
            }
            closeEcs();
            setScreen(ScreenType.MENU);
            Popup.popupErreur(this, e.toString(), uiStage);
        }
    }

    public void nouveauECS(ClientConnexion clientConnexion) throws Exception {
        if (ecsEngine != null) {
            closeEcs();
        }
        ecsEngine = new ECSEngine(this, clientConnexion);
        onEcsEngineInitializes.forEach((ecsEngineConsumer) -> ecsEngineConsumer.accept(ecsEngine));
        onEcsEngineInitializes.clear();
    }

    @Override
    public void closeEcs() {
        Gdx.app.postRunnable(() -> {
            if (ecsEngine != null) {
                ecsEngine.clearAllEntity();
                ecsEngine.dispose();
            }
            ecsEngine = null;
        });
    }

    @Override
    public void displayErrorPopup(String message) {
        Gdx.app.postRunnable(() -> Popup.popupErreur(this, message, uiStage));
    }

    public void rejoindreMulti(String host, int port) throws Exception {
        synchronized (this) {
            if (ecsEngine == null) {
                ConnexionConfig connexionConfig = ConnexionConfig.builder()
                        .setHost(host)
                        .setPort(port, false)
                        .build();
                ClientConnexion clientConnexionHandler = ClientConnexionExtern.create(connexionConfig, clientExecute, this);
                nouveauECS(clientConnexionHandler);
                authControllerClient.sendAuthAndJoinServer(clientConnexionHandler, verifyAccessToken);

            }
        }
    }

    /**
     * Load and join a world, if a world with this save name didn't existe, create a new one. Seed must be spesified if it is a new world
     *
     * @param saveName The save name of the world.
     * @param seed     The seed of the new world. of null if don't create a new world
     */
    public void rejoindreSoloServeur(String saveName, @Null Long seed) throws Exception {
        synchronized (this) {
            if (ecsEngine == null) {
                ConnexionConfig.ConnexionConfigBuilder connexionConfigBuilder = ConnexionConfig.builder();
                if (seed != null) {
                    connexionConfigBuilder.setSeed(seed);
                }
                ConnexionConfig connexionConfig = connexionConfigBuilder
                        .setRootPath(DataCtrl.getRootPath())
                        .setSaveName(saveName)
                        .setVerifyAccessToken(verifyAccessToken)
                        .setClientExecute(clientExecute)
                        .setRootRegistry(rootRegistry)
                        .build();
                ClientConnexion clientConnexion = new ClientConnexionIntern(this, connexionConfig);
                nouveauECS(clientConnexion);
                authControllerClient.sendAuthAndJoinServer(clientConnexion, verifyAccessToken);
            }
        }
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }

    public <T extends BaseSystem> T getSystem(Class<T> type) {
        return ecsEngine.getSystem(type);
    }
    public SystemsAdminClient getSystemsAdminClient() {
        return ecsEngine.getSystemsAdminClient();
    }

    public ClientExecute getClientExecute() {
        return clientExecute;
    }

    public ClientConnexion getClientConnexion() {
        return ecsEngine.getClientConnexion();
    }
    public void nextFrame(Runnable runnable) {
        if (ecsEngine != null) {
            ecsEngine.nextFrame(runnable);
        }
    }

    @Override
    public void writeToConsole(String s) {
        gameScreen.writeToConsole(s);
    }

    @Override
    public Registry<?> getRootRegistry() {
        return rootRegistry;
    }

    public SerializerController getSerializerController() {
        return ecsEngine.getSerializerController();
    }

    public AuthControllerClient getAuthControllerClient() {
        return authControllerClient;
    }

    /**
     * Safely execute on game running. If the ecs engine is not initialized, execute when the ecs engine is initialized,
     * or execute directly.
     */
    @Override
    public void getWorldOr(Consumer<GetWorld> ecsEngineConsumer) {
        if (getEcsEngine() != null) {
            ecsEngineConsumer.accept(getEcsEngine());
        } else {
            onEcsEngineInitializes.add(ecsEngineConsumer);
        }
    }

    public void setVerifyAccessToken(boolean verifyAccessToken) {
        this.verifyAccessToken = verifyAccessToken;
    }

    @Override
    public ExecuteOnContext getExecuteOnContext() {
        return executeOnContextClient;
    }
}
