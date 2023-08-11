package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.component.InfCellComponent;
import ch.realmtech.game.ecs.component.InfChunkComponent;
import ch.realmtech.game.ecs.component.InfMapComponent;
import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class CellHoverSystem extends BaseSystem {
    private final static String TAG = CellHoverSystem.class.getSimpleName();
    @Wire(name = "context")
    RealmTech context;

    ComponentMapper<InfCellComponent> mCell;
    ComponentMapper<InfChunkComponent> mChunk;

    @Override
    protected void begin() {
        super.begin();
        context.getGameStage().getBatch().begin();
    }

    @Override
    protected void end() {
        super.end();
        context.getGameStage().getBatch().end();
    }

    @Override
    protected void processSystem() {
        try {
            int[] infChunks = context.getEcsEngine().getMapEntity().getComponent(InfMapComponent.class).infChunks;
            Vector2 screenCoordinate = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            Vector3 gameCoordinate = context.getGameStage().getCamera().unproject(new Vector3(screenCoordinate, 0));
            int chunk = world.getSystem(MapSystem.class).getChunk(infChunks, gameCoordinate.x, gameCoordinate.y);
            int cell = world.getSystem(MapSystem.class).getTopCell(chunk, MapSystem.getInnerChunk(gameCoordinate.x), MapSystem.getInnerChunk(gameCoordinate.y));
            InfChunkComponent infChunkComponent = mChunk.get(chunk);
            InfCellComponent infCellComponent = mCell.get(cell);
            TextureAtlas.AtlasRegion region = context.getTextureAtlas().findRegion("cellOver-01");
            context.getGameStage().getBatch().draw(region,
                    MapSystem.getWorldPos(infChunkComponent.chunkPosX, infCellComponent.innerPosX),
                    MapSystem.getWorldPos(infChunkComponent.chunkPosY, infCellComponent.innerPosY),
                    RealmTech.PPM * RealmTech.UNITE_SCALE / 2,
                    RealmTech.PPM * RealmTech.UNITE_SCALE / 2
            );
        } catch (Exception e) {
            Gdx.app.log(TAG, e.getMessage());
        }
    }
}
