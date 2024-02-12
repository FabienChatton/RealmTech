package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.RealmTech;
import ch.realmtech.core.game.ecs.plugin.SystemsAdminClient;
import ch.realmtech.server.ecs.component.InfMapComponent;
import ch.realmtech.server.ecs.system.MapManager;
import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

public class CellHoverSystem extends BaseSystem {
    @Wire(name = "context")
    private RealmTech context;
    @Wire
    private SystemsAdminClient systemsAdminClient;

    @Override
    protected void processSystem() {
        int[] infChunks = context.getEcsEngine().getMapEntity().getComponent(InfMapComponent.class).infChunks;
        Vector2 screenCoordinate = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        Vector2 gameCoordinate = context.getEcsEngine().getGameCoordinate(screenCoordinate);
        int worldPosX = MapManager.getWorldPos(gameCoordinate.x);
        int worldPosY = MapManager.getWorldPos(gameCoordinate.y);

        int chunk = systemsAdminClient.mapManager.getChunk(MapManager.getChunkPos(worldPosX), MapManager.getChunkPos(worldPosY), infChunks);
        if (chunk == -1) return;
        int topCell = systemsAdminClient.mapManager.getTopCell(chunk, MapManager.getInnerChunk(worldPosX), MapManager.getInnerChunk(worldPosY));
        if (topCell == -1) return;
        TextureAtlas.AtlasRegion region = context.getTextureAtlas().findRegion("cellOver-01");
        context.getGameStage().getBatch().draw(
                region,
                worldPosX,
                worldPosY,
                RealmTech.PPM * RealmTech.UNITE_SCALE / 2,
                RealmTech.PPM * RealmTech.UNITE_SCALE / 2
        );


    }
}
