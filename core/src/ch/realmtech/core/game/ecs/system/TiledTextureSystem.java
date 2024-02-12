package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.RealmTech;
import ch.realmtech.core.game.ecs.component.TiledTextureComponent;
import ch.realmtech.server.ecs.component.CellComponent;
import ch.realmtech.server.ecs.component.InfChunkComponent;
import ch.realmtech.server.ecs.system.MapManager;
import ch.realmtech.server.level.cell.CellBehavior;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

@All(InfChunkComponent.class)
public class TiledTextureSystem extends IteratingSystem {

    @Wire(name = "context")
    private RealmTech context;
    private ComponentMapper<InfChunkComponent> mChunk;
    private ComponentMapper<CellComponent> mCell;
    private ComponentMapper<TiledTextureComponent> mTiledTexture;
    @Override
    protected void process(int entityId) {
        InfChunkComponent infChunkComponent = mChunk.get(entityId);
        for (int i = 0; i < infChunkComponent.infCellsId.length; i++) {
            int cellId = infChunkComponent.infCellsId[i];
            if (mTiledTexture.has(cellId)) {
                continue;
            }
            CellComponent cellComponent = mCell.get(cellId);
            int worldX = MapManager.getWorldPos(infChunkComponent.chunkPosX, cellComponent.getInnerPosX());
            int worldY = MapManager.getWorldPos(infChunkComponent.chunkPosY, cellComponent.getInnerPosY());
            if (cellComponent.cellRegisterEntry.getCellBehavior().getTiledTextureX() > 0 || cellComponent.cellRegisterEntry.getCellBehavior().getTiledTextureY() > 0) {
                CellBehavior cellBehavior = cellComponent.cellRegisterEntry.getCellBehavior();
                String tiledTextureRegionName = cellComponent.cellRegisterEntry.getTextureRegionName()
                        + "-"
                        + (Math.abs(worldX) % (cellBehavior.getTiledTextureX() + 1))
                        + "-"
                        + (Math.abs(worldY) % (cellBehavior.getTiledTextureY() + 1));
                TextureRegion textureRegion = context.getTextureAtlas().findRegion(tiledTextureRegionName);
                mTiledTexture.create(cellId).set(textureRegion);
            }
        }
    }

    public void removeAllTiledTextureComponent() {
        IntBag entities = world.getAspectSubscriptionManager().get(Aspect.all(TiledTextureComponent.class)).getEntities();
        int[] entitiesData = entities.getData();
        for (int i = 0; i < entities.size(); i++) {
            int entityId = entitiesData[i];
            mTiledTexture.remove(entityId);
        }
    }
}
