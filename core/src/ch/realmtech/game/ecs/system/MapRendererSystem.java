package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.component.InfCellComponent;
import ch.realmtech.game.ecs.component.InfChunkComponent;
import ch.realmtech.game.ecs.component.InfMapComponent;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
@All(InfMapComponent.class)
public class MapRendererSystem extends IteratingSystem {
    @Wire(name = "context")
    private RealmTech context;
    private ComponentMapper<InfMapComponent> mMap;
    private ComponentMapper<InfChunkComponent> mChunk;
    private ComponentMapper<InfCellComponent> mCell;
    private OrthogonalTiledMapRenderer mapRenderer;

    @Override
    protected void begin() {
        context.getGameStage().getBatch().begin();
    }

    @Override
    protected void process(int mapId) {
        InfMapComponent infMapComponent = mMap.get(mapId);
        for (int i = 0; i < infMapComponent.infChunks.length; i++) {
            int chunkId = infMapComponent.infChunks[i];
            InfChunkComponent infChunkComponent = mChunk.get(chunkId);
            for (int j = 0; j < infChunkComponent.infCellsId.length; j++) {
                int cellId = infChunkComponent.infCellsId[j];
                InfCellComponent infCellComponent = mCell.get(cellId);
                int worldX = MapSystem.getWorldPoss(infChunkComponent.chunkPossX, infCellComponent.innerPosX);
                int worldY = MapSystem.getWorldPoss(infChunkComponent.chunkPossY, infCellComponent.innerPosY);
                TextureRegion textureRegion = infCellComponent.cellRegisterEntry.getTextureRegion();
                context.getGameStage().getBatch().draw(textureRegion, worldX, worldY, textureRegion.getRegionWidth() * RealmTech.UNITE_SCALE, textureRegion.getRegionHeight() * RealmTech.UNITE_SCALE);
            }
        }
    }

    @Override
    protected void end() {
        context.getGameStage().getBatch().end();
    }
}