package ch.realmtech;

import ch.realmtech.ecs.ECSEngine;
import ch.realmtech.healper.HealperSetContext;
import ch.realmtech.input.InputManager;
import ch.realmtech.screen.AbstractScreen;
import ch.realmtech.screen.ScreenType;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.EnumMap;

public final class RealmTech extends Game{
    public final static float WORLD_WIDTH = 16f;
    public final static float WORLD_HEIGHT = 9f;
    public final static int SCREEN_WIDTH = 1024;
    public final static int SCREEN_HEIGHT = 576;
    public final static float PPM = SCREEN_WIDTH / WORLD_WIDTH;
    public final static float UNITE_SCALE = 1 / 32f;
    private final String TAG = RealmTech.class.getSimpleName();
    private InputManager inputManager;
    private EnumMap<ScreenType, AbstractScreen> screenCash;
    private AssetManager assetManager;
    private Stage gameStage;
    private Stage uiStage;
    private Skin skin;
	private ECSEngine ecsEngine;
    public World world;

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        assetManager = new AssetManager();
        initSkin();
        initHealper();
        initMap();
        Box2D.init();
        inputManager = InputManager.getInstance();
        world = new World(new Vector2(0, 0), true);
        screenCash = new EnumMap<>(ScreenType.class);
        gameStage = new Stage(
                new FitViewport(WORLD_WIDTH, WORLD_HEIGHT,
                        new OrthographicCamera(SCREEN_WIDTH, SCREEN_HEIGHT)));
        uiStage = new Stage(
                new FitViewport(SCREEN_WIDTH, SCREEN_HEIGHT,
                        new OrthographicCamera(SCREEN_WIDTH, SCREEN_HEIGHT)));
        ecsEngine = new ECSEngine(this);
        setScreen(ScreenType.LOADING);
        setScreen(ScreenType.LOADING);
    }

    private void initHealper() {
        HealperSetContext.setContext(this);
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
        assetManager.load("map/mapTest.tmx", TiledMap.class);
        Gdx.app.debug(TAG, "Map test chargé");
    }

    public void setScreen(final ScreenType screenType) {
        AbstractScreen screen = screenCash.get(screenType);
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

    public InputManager getInputManager() {
        return inputManager;
    }

    @Override
    public void dispose() {
        gameStage.dispose();
        uiStage.dispose();
        assetManager.dispose();
    }
}
