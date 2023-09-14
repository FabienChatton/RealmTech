package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import ch.realmtechCommuns.ecs.component.CellBeingMineComponent;
import ch.realmtechCommuns.ecs.component.InfCellComponent;
import ch.realmtechCommuns.ecs.component.InfChunkComponent;
import ch.realmtechCommuns.ecs.system.MapSystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

@All({CellBeingMineComponent.class, InfCellComponent.class})
public class CellBeingMineRenderSystem extends IteratingSystem {

    @Wire(name = "context")
    RealmTech context;

    ComponentMapper<CellBeingMineComponent> mCellBeingMine;
    ComponentMapper<InfCellComponent> mCell;
    ComponentMapper<InfChunkComponent> mChunk;

    @Override
    protected void begin() {
        super.begin();
        context.getGameStage().getBatch().begin();
    }

    @Override
    protected void process(int entityId) {
//        CellBeingMineComponent cellBeingMineComponent = mCellBeingMine.get(entityId);
//        InfCellComponent infCellComponent = mCell.get(entityId);
//        int chunkId = world.getSystem(MapSystem.class).findChunk(MapSystem.getChunkInUse(context), entityId);
//        InfChunkComponent infChunkComponent = mChunk.get(chunkId);
//        int worldPosX = MapSystem.getWorldPos(infChunkComponent.chunkPosX, infCellComponent.getInnerPosX());
//        int worldPosY = MapSystem.getWorldPos(infChunkComponent.chunkPosY, infCellComponent.getInnerPosY());
//        TextureRegion texture = getTextureViaPourCent(cellBeingMineComponent);
//
//        context.getGameStage().getBatch().draw(
//                texture,
//                worldPosX,
//                worldPosY,
//                RealmTech.PPM * RealmTech.UNITE_SCALE / 2,
//                RealmTech.PPM * RealmTech.UNITE_SCALE / 2
//        );
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
            pourQuinze = Math.max((int) ((cellBeingMineComponent.currentStep) * 15f / (float) cellBeingMineComponent.step), 1);
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
