package ch.realmtech.screen;

import ch.realmtech.RealmTech;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

import java.io.IOException;

public class GameScreen extends AbstractScreen {

    private final OrthogonalTiledMapRenderer mapRenderer;
    private final Box2DDebugRenderer box2DDebugRenderer;


    public GameScreen(RealmTech context) throws IOException {
        super(context);
        //tiledMap = context.getAssetManager().get("map/mapTest.tmx", TiledMap.class);
        mapRenderer = new OrthogonalTiledMapRenderer(context.getWorldMap(), RealmTech.UNITE_SCALE, context.getGameStage().getBatch());
        box2DDebugRenderer = new Box2DDebugRenderer();
        context.getEcsEngine().spawnPlayer(context.getWorldMap().getProperties().get("spawn-point", Vector2.class));
    }

    public void setMapRenderer(TiledMap map) {
        mapRenderer.setMap(map);
    }

    @Override
    public void show() {
        super.show();
        Gdx.input.setInputProcessor(context.getInputManager());
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            context.setScreen(ScreenType.GAME_PAUSE);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            try {
                context.newSave("test");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //context.getSave().getTiledMap().creerMapAleatoire();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F2)) {
            context.getEcsEngine().spawnPlayer(context.getWorldMap().getProperties().get("spawn-point", Vector2.class));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            try {
                context.writeWorldMap();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void draw() {
        mapRenderer.setView((OrthographicCamera) context.getGameStage().getCamera());
        mapRenderer.render();
        ecsEngine.process(Gdx.graphics.getDeltaTime());
        uiStage.draw();
        box2DDebugRenderer.render(context.physicWorld, gameCamera.combined);
    }

    @Override
    public void dispose() {
        super.dispose();
        box2DDebugRenderer.dispose();
    }
}
