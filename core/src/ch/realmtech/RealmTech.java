package ch.realmtech;

import ch.realmtech.discord.Discord;
import ch.realmtech.game.ecs.ECSEngine;
import ch.realmtech.game.ecs.system.SoundManager;
import ch.realmtech.helper.SetContext;
import ch.realmtech.input.InputMapper;
import ch.realmtech.options.RealmTechDataCtrl;
import ch.realmtech.screen.AbstractScreen;
import ch.realmtech.screen.ScreenType;
import com.artemis.BaseSystem;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import java.io.IOException;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;

public final class RealmTech extends Game{
    public final static float WORLD_WIDTH = 16f;
    public final static float WORLD_HEIGHT = 9f;
    public final static int SCREEN_WIDTH = 1024;
    public final static int SCREEN_HEIGHT = 576;
    public final static float PPM = SCREEN_WIDTH / WORLD_WIDTH;
    public final static float UNITE_SCALE = 1 / 32f;
    private final String TAG = RealmTech.class.getSimpleName();
    private InputMapper inputMapper;
    private EnumMap<ScreenType, AbstractScreen> screenCash;
    private AssetManager assetManager;
    private Stage gameStage;
    private Stage uiStage;
    private Skin skin;
	private ECSEngine ecsEngine;
    private Discord discord;

    private TextureAtlas textureAtlas;
    private RealmTechDataCtrl realmTechDataCtrl;

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        try {
            realmTechDataCtrl = new RealmTechDataCtrl();
        } catch (IOException e) {
            Gdx.app.error(TAG, "La hiérarchie des dossier n'a pas pu être créer correctement", e);
            Gdx.app.exit();
        }
        assetManager = new AssetManager();
        initSkin();
        initHealper();
        initMap();
        initSound();
        Box2D.init();
        discord = new Discord(Thread.currentThread());
        discord.init();
        gameStage = new Stage(
                new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT,
                        new OrthographicCamera(SCREEN_WIDTH, SCREEN_HEIGHT)));
        uiStage = new Stage(
                new ExtendViewport(SCREEN_WIDTH, SCREEN_HEIGHT,
                        new OrthographicCamera(SCREEN_WIDTH, SCREEN_HEIGHT)));

        inputMapper = InputMapper.getInstance(this);
        screenCash = new EnumMap<>(ScreenType.class);
        setScreen(ScreenType.LOADING);
    }

    public void loadingFinish() {
        ecsEngine = new ECSEngine(this);
        setScreen(ScreenType.MENU);
    }

    private void initHealper() {
        SetContext.setContext(this);
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
		Gdx.app.debug(TAG, "skin initialise");
    }

    private void initMap() {
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(assetManager.getFileHandleResolver()));
        assetManager.load("texture/atlas/texture.atlas", TextureAtlas.class);
        Gdx.app.debug(TAG, "Map test chargé");
    }

    private void initSound() {
        SoundManager.initAsset(assetManager);
    }

    public void setScreen(final ScreenType screenType) {
        AbstractScreen screen = screenCash.get(screenType);
        ScreenType oldScreenType = null;
        for (Map.Entry<ScreenType, AbstractScreen> screenTypeAbstractScreenEntry : screenCash.entrySet()) {
            if (screenTypeAbstractScreenEntry.getValue() == getScreen()) {
                oldScreenType = screenTypeAbstractScreenEntry.getKey();
                break;
            }
        }
        if (screen == null) {
            try {
                screen = screenType.screenClass.getConstructor(RealmTech.class).newInstance(this);
                Gdx.app.debug(TAG, "screen : " + screenType + " creer");
                screenCash.put(screenType, screen);
                Gdx.app.debug(TAG, "screen : " + screenType + " ajoute au cash des screens");
            } catch (ReflectiveOperationException e) {
                Gdx.app.error(TAG, "La class " + screenType + " n'a pas pu etre cree", e);
            }
        }
        screen.setOldScreen(oldScreenType);
        Gdx.app.debug(TAG, "Changement d'ecran vers " + screenType);
        super.setScreen(screen);
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
        realmTechDataCtrl.saveConfig();
        discord.stop();
    }

    public TextureAtlas getTextureAtlas() {
        if (textureAtlas == null) {
            textureAtlas = assetManager.get("texture/atlas/texture.atlas");
        }
        return textureAtlas;
    }

    public void quiteAndSave() {
        try {
            ecsEngine.saveInfMap();
            ecsEngine.savePlayerInventory();
            ecsEngine.dispose();
            ecsEngine = new ECSEngine(this);
        } catch (IOException e) {
            Gdx.app.error(TAG, "impossible de sauvegarder", e);
        } finally {
            setScreen(ScreenType.MENU);
            screenCash.remove(ScreenType.GAME_SCREEN);
        }
    }

    public void drawGameScreen() {
        if (screenCash.containsKey(ScreenType.GAME_SCREEN)) {
            screenCash.get(ScreenType.GAME_SCREEN).draw();
        }
    }

    public void process(float deltaTime) {
        ecsEngine.process(deltaTime);
    }

    public void loadInfFile(Path path) throws IOException {
        ecsEngine.loadInfFile(path);
    }

    public RealmTechDataCtrl getRealmTechDataCtrl() {
        return realmTechDataCtrl;
    }
    public <T extends BaseSystem> T getSystem(Class<T> type) {
        return ecsEngine.getSystem(type);
    }
}
