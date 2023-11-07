package ch.realmtech;

import ch.realmtech.discord.Discord;
import ch.realmtech.game.ecs.ECSEngine;
import ch.realmtech.game.netty.ClientExecuteContext;
import ch.realmtech.game.netty.RealmTechClientConnexionHandler;
import ch.realmtech.helper.Popup;
import ch.realmtech.input.InputMapper;
import ch.realmtech.screen.AbstractScreen;
import ch.realmtech.screen.GameScreen;
import ch.realmtech.screen.ScreenType;
import ch.realmtech.sound.SoundManager;
import ch.realmtechServer.netty.ConnexionBuilder;
import ch.realmtechServer.options.DataCtrl;
import ch.realmtechServer.packet.clientPacket.ClientExecute;
import ch.realmtechServer.packet.serverPacket.DemandeDeConnexionJoueurPacket;
import com.artemis.BaseSystem;
import com.badlogic.gdx.Application;
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
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public final class RealmTech extends Game{
    public final static float WORLD_WIDTH = 16f;
    public final static float WORLD_HEIGHT = 9f;
    public final static int SCREEN_WIDTH = 1024;
    public final static int SCREEN_HEIGHT = 576;
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
    private DataCtrl dataCtrl;
    private SoundManager soundManager;
    private ClientExecute clientExecute;
    private ScreenType currentScreenType;
    private GameScreen gameScreen;

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        try {
            dataCtrl = new DataCtrl();
        } catch (IOException e) {
            logger.error("La hiérarchie des dossier n'a pas pu être créer correctement", e);
            Gdx.app.exit();
        }
        assetManager = new AssetManager();
        initSkin();
        initMap();
        initSound();
        Box2D.init();
        soundManager = new SoundManager(this);
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
    }

    public void loadingFinish() {
        setScreen(ScreenType.MENU);
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
        SoundManager.initAsset(assetManager);
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
                if (currentScreen != null) currentScreen.dispose();
            }
            currentScreenType = screenType;
        } catch (ReflectiveOperationException e) {
            logger.error("La class {} n'a pas pu etre cree", screenType, e);
        }
    }

    public ScreenType getScreenType() {
        return currentScreenType;
    }

	public Skin getSkin() {
		return skin;
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
        dataCtrl.saveConfig();
        discord.stop();
        supprimeECS();
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
            ecsEngine.process(deltaTime);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            if (uiStage.getBatch().isDrawing()) {
                uiStage.getBatch().end();
            }
            if (gameStage.getBatch().isDrawing()) {
                gameStage.getBatch().end();
            }
            supprimeECS();
            setScreen(ScreenType.MENU);
            Popup.popupErreur(this, e.toString(), uiStage);
        }
    }

    public void nouveauECS(RealmTechClientConnexionHandler clientConnexionHandler) throws IOException {
        if (ecsEngine != null) {
            supprimeECS();
        }
        ecsEngine = new ECSEngine(this, clientConnexionHandler);
    }

    public void supprimeECS() {
        if (ecsEngine != null) {
            ecsEngine.clearAllEntity();
            ecsEngine.dispose();
        }
        ecsEngine = null;
    }

    public void rejoindreMulti(String host, int port) throws Exception {
        synchronized (this) {
            if (ecsEngine == null) {
                RealmTechClientConnexionHandler clientConnexionHandler = new RealmTechClientConnexionHandler(new ConnexionBuilder().setHost(host).setPort(port), clientExecute, false, this);
                nouveauECS(clientConnexionHandler);
                clientConnexionHandler.sendAndFlushPacketToServer(new DemandeDeConnexionJoueurPacket());
            }
        }
    }

    public void rejoindreSoloServeur(String saveName) throws Exception {
        synchronized (this) {
            if (ecsEngine == null) {
                ConnexionBuilder connexionBuilder = new ConnexionBuilder().setSaveName(saveName);
                RealmTechClientConnexionHandler clientConnexionHandler = new RealmTechClientConnexionHandler(connexionBuilder, clientExecute, true, this);
                nouveauECS(clientConnexionHandler);
                clientConnexionHandler.sendAndFlushPacketToServer(new DemandeDeConnexionJoueurPacket());
            }
        }
    }

    public DataCtrl getDataCtrl() {
        return dataCtrl;
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }

    public <T extends BaseSystem> T getSystem(Class<T> type) {
        return ecsEngine.getSystem(type);
    }

    public ClientExecute getClientExecute() {
        return clientExecute;
    }
    public RealmTechClientConnexionHandler getConnexionHandler() {
        return ecsEngine.getConnexionHandler();
    }
    public void nextFrame(Runnable runnable) {
        if (ecsEngine != null) {
            ecsEngine.nextFrame(runnable);
        }
    }

    public void writeToConsole(String s) {
        gameScreen.writeToConsole(s);
    }
}
