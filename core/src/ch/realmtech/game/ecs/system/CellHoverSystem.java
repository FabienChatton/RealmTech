package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.component.InfCellComponent;
import ch.realmtech.game.ecs.component.InfChunkComponent;
import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

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
        Vector2 screenCoordinate = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        int chunk = MapSystem.getChunk(context, screenCoordinate);
        if (chunk == -1) return;
        int cell = MapSystem.getTopCell(context, chunk, screenCoordinate);
        if (cell == -1) return;
        InfChunkComponent infChunkComponent = mChunk.get(chunk);
        InfCellComponent infCellComponent = mCell.get(cell);
        TextureAtlas.AtlasRegion region = context.getTextureAtlas().findRegion("cellOver-01");
        context.getGameStage().getBatch().draw(region,
                MapSystem.getWorldPos(infChunkComponent.chunkPosX, infCellComponent.innerPosX),
                MapSystem.getWorldPos(infChunkComponent.chunkPosY, infCellComponent.innerPosY),
                RealmTech.PPM * RealmTech.UNITE_SCALE / 2,
                RealmTech.PPM * RealmTech.UNITE_SCALE / 2
        );
    }
}
