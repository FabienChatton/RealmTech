package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.component.CellBeingMineComponent;
import ch.realmtech.game.ecs.component.InfCellComponent;
import ch.realmtech.game.ecs.component.InfChunkComponent;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

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
        CellBeingMineComponent cellBeingMineComponent = mCellBeingMine.get(entityId);
        InfCellComponent infCellComponent = mCell.get(entityId);
        Vector2 screenCoordinate = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        Vector3 gameCoordinate = MapSystem.getGameCoordinate(context, screenCoordinate);
        int chunk = world.getSystem(MapSystem.class).getChunk(MapSystem.getChunkInUse(context), gameCoordinate.x, gameCoordinate.y);
        InfChunkComponent infChunkComponent = mChunk.get(chunk);
        int worldPosX = MapSystem.getWorldPos(infChunkComponent.chunkPosX, infCellComponent.innerPosX);
        int worldPosY = MapSystem.getWorldPos(infChunkComponent.chunkPosY, infCellComponent.innerPosY);
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
        int pourQuinze = (int) ((cellBeingMineComponent.currentStep - 1f) * 15f / (float) cellBeingMineComponent.step) + 1;
        String textureFormat;
        if (pourQuinze < 10) {
            textureFormat = String.format("cell-breaking-stage-%02d", pourQuinze);
        } else {
            textureFormat = String.format("cell-breaking-stage-%d", pourQuinze);
        }
        System.out.println(textureFormat);
        return context.getTextureAtlas().findRegion(textureFormat);
    }
}
