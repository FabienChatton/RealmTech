package ch.realmtech.screen;

import ch.realmtech.RealmTech;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class GameScreen extends AbstractScreen {
    private TiledMap tiledMap;
    private final OrthogonalTiledMapRenderer mapRenderer;
    public GameScreen(RealmTech context) {
        super(context);
        tiledMap = context.getAssetManager().get("map/mapTest.tmx", TiledMap.class);
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, RealmTech.UNITE_SCALE, context.getGameStage().getBatch());
        context.getEcsEngine().createPlayer();
    }

    @Override
    public void show() {
        super.show();
        Gdx.input.setInputProcessor(gameStage);
    }

    @Override
    public void draw() {
        mapRenderer.setView((OrthographicCamera) context.getGameStage().getCamera());
        mapRenderer.render();
        ecsEngine.update(Gdx.graphics.getDeltaTime());
        uiStage.draw();
    }
}
