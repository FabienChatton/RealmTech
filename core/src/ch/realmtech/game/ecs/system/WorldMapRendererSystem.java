package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class WorldMapRendererSystem extends BaseSystem {
    @Wire(name = "context")
    private RealmTech context;
    private OrthogonalTiledMapRenderer mapRenderer;

    @Override
    protected void processSystem() {
        mapRenderer.setView((OrthographicCamera) context.getGameStage().getCamera());
        mapRenderer.render();
    }

    public void setMapRenderer(TiledMap map) {
        if (mapRenderer != null) {
            mapRenderer.setMap(map);
        } else {
            mapRenderer = new OrthogonalTiledMapRenderer(map, RealmTech.UNITE_SCALE, context.getGameStage().getBatch());
        }
    }
}
