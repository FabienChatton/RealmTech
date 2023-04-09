package ch.realmtech;

import ch.realmtech.game.ecs.ECSEngine;
import ch.realmtech.game.ecs.component.SaveComponent;
import ch.realmtech.game.ecs.system.WorldMapManager;
import ch.realmtech.game.io.Save;
import ch.realmtech.game.io.SaveFactory;
import ch.realmtech.game.level.map.WorldMap;
import ch.realmtech.helper.HelperSetContext;
import ch.realmtech.input.InputMapper;
import ch.realmtech.screen.AbstractScreen;
import ch.realmtech.screen.GameScreen;
import ch.realmtech.screen.ScreenType;
import com.artemis.Entity;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;

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
    public World physicWorld;
    private Save workingSave;

    private TextureAtlas textureAtlas;

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        assetManager = new AssetManager();
        initSkin();
        initHealper();
        initMap();
        Box2D.init();
        gameStage = new Stage(
                new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT,
                        new OrthographicCamera(SCREEN_WIDTH, SCREEN_HEIGHT)));
        uiStage = new Stage(
                new ExtendViewport(SCREEN_WIDTH, SCREEN_HEIGHT,
                        new OrthographicCamera(SCREEN_WIDTH, SCREEN_HEIGHT)));
        physicWorld = new World(new Vector2(0, 0), true);
        inputMapper = InputMapper.getInstance(this);
        screenCash = new EnumMap<>(ScreenType.class);
        setScreen(ScreenType.LOADING);
    }

    public void loadingFinish() {
        ecsEngine = new ECSEngine(this);
        setScreen(ScreenType.MENU);
    }

    private void initHealper() {
        HelperSetContext.setContext(this);
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

    public InputMapper getInputManager() {
        return inputMapper;
    }

    @Override
    public void dispose() {
        gameStage.dispose();
        uiStage.dispose();
        assetManager.dispose();
    }

    public TextureAtlas getTextureAtlas() {
        if (textureAtlas == null) {
            textureAtlas = assetManager.get("texture/atlas/texture.atlas");
        }
        return textureAtlas;
    }

    public void loadSave(File saveFile) throws IOException {
        workingSave = SaveFactory.loadSave(saveFile,this);
    }

    public void writeWorldMap() throws IOException {
        ecsEngine.saveWorldMap();
//        if (workingSave != null) {
//            workingSave.saveLast();
//        } else {
//            Gdx.app.debug(TAG, "La save n'a pas put être sauvegarde car elle n'existe pas");
//        }
    }

    public void newSave(String saveName) throws IOException {
        ecsEngine.newWorldMap(saveName + ".rts");
        //workingSave = SaveFactory.genereteNewSave(Gdx.files.local(saveName + ".rts").file(), this);
    }

    public void quiteAndSave() {
        try {
            writeWorldMap();
        } catch (IOException e) {
            throw new RuntimeException(e);
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

    public SaveComponent getWorkingSave() {
        //return workingSave;
        return ecsEngine.getWorkingSave();
    }

    public WorldMap getWorldMap() {
//        Save save = getWorkingSave();
//        if (save != null) return save.getTiledMap();
        return ecsEngine.getWorkingMap();
    }

    public WorldMapManager getWorldMapManager() {
        return ecsEngine.getWorldMapManager();
    }

    public void setMapRenderer(TiledMap map) {
        if (screenCash.containsKey(ScreenType.GAME_SCREEN)) {
            ((GameScreen) screenCash.get(ScreenType.GAME_SCREEN)).setMapRenderer(map);
        }
    }

    public Entity getPlayer() {
        return ecsEngine.getPlayer();
    }
}
