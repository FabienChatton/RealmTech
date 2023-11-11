package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.RealmTech;
import ch.realmtech.core.game.ecs.plgin.SystemsAdminClient;
import ch.realmtech.server.ecs.component.CellBeingMineComponent;
import ch.realmtech.server.ecs.component.InfCellComponent;
import ch.realmtech.server.ecs.component.InfChunkComponent;
import ch.realmtech.server.ecs.component.InfMapComponent;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.ecs.system.MapManager;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

@All({CellBeingMineComponent.class, InfCellComponent.class})
public class CellBeingMineRenderSystem extends IteratingSystem {

    @Wire(name = "context")
    private RealmTech context;
    @Wire
    private SystemsAdminClient systemsAdminClient;
    private ComponentMapper<CellBeingMineComponent> mCellBeingMine;
    private ComponentMapper<InfCellComponent> mCell;
    private ComponentMapper<InfChunkComponent> mChunk;

    @Override
    protected void begin() {
        super.begin();
        context.getGameStage().getBatch().begin();
    }

    @Override
    protected void process(int entityId) {
        CellBeingMineComponent cellBeingMineComponent = mCellBeingMine.get(entityId);
        InfCellComponent infCellComponent = mCell.get(entityId);
        int[] infChunks = context.getEcsEngine().getMapEntity().getComponent(InfMapComponent.class).infChunks;
        int chunkId = systemsAdminClient.mapManager.findChunk(infChunks, entityId);
        InfChunkComponent infChunkComponent = mChunk.get(chunkId);
        int worldPosX = MapManager.getWorldPos(infChunkComponent.chunkPosX, infCellComponent.getInnerPosX());
        int worldPosY = MapManager.getWorldPos(infChunkComponent.chunkPosY, infCellComponent.getInnerPosY());
        TextureRegion texture = getTextureViaPourCent(cellBeingMineComponent);

        context.getGameStage().getBatch().draw(
                texture,
                worldPosX,
                worldPosY,
                RealmTech.PPM * RealmTech.UNITE_SCALE / 2,
                RealmTech.PPM * RealmTech.UNITE_SCALE / 2
        );
    }

    @Override
    protected void end() {
        super.end();
        context.getGameStage().getBatch().end();
    }

    private TextureRegion getTextureViaPourCent(CellBeingMineComponent cellBeingMineComponent) {
        int pourQuinze;
        if (cellBeingMineComponent.step == CellBeingMineComponent.INFINITE_MINE) {
            pourQuinze = 1;
        } else {
            pourQuinze = Math.min(15, Math.max((int) ((cellBeingMineComponent.currentStep) * 15f / (float) cellBeingMineComponent.step), 1));
        }
        String textureFormat;
        if (pourQuinze < 10) {
            textureFormat = String.format("cell-breaking-stage-%02d", pourQuinze);
        } else {
            textureFormat = String.format("cell-breaking-stage-%d", pourQuinze);
        }
        return context.getTextureAtlas().findRegion(textureFormat);
    }
}
