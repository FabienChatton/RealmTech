package ch.realmtech.screen;

import ch.realmtech.RealmTech;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

public class GameScreen extends AbstractScreen {

    private final OrthogonalTiledMapRenderer mapRenderer;
    private final Box2DDebugRenderer box2DDebugRenderer;

    public GameScreen(RealmTech context) {
        super(context);
        //tiledMap = context.getAssetManager().get("map/mapTest.tmx", TiledMap.class);
        context.gameMap.creerMapAleatoire();
        mapRenderer = new OrthogonalTiledMapRenderer(context.gameMap, RealmTech.UNITE_SCALE, context.getGameStage().getBatch());
        box2DDebugRenderer = new Box2DDebugRenderer();
        context.getEcsEngine().createPlayer();

    }

    @Override
    public void show() {
        super.show();
        Gdx.input.setInputProcessor(context.getInputManager());
    }

    @Override
    public void draw() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            context.gameMap.creerMapAleatoire();
        }
        mapRenderer.setView((OrthographicCamera) context.getGameStage().getCamera());
        mapRenderer.render();
        ecsEngine.update(Gdx.graphics.getDeltaTime());
        uiStage.draw();
        box2DDebugRenderer.render(context.world, gameCamera.combined);
    }

    @Override
    public void dispose() {
        super.dispose();
        box2DDebugRenderer.dispose();
    }
}
