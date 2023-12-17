package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.RealmTech;
import ch.realmtech.server.ecs.component.CellComponent;
import ch.realmtech.server.ecs.component.InfChunkComponent;
import ch.realmtech.server.ecs.component.InfMapComponent;
import ch.realmtech.server.ecs.system.MapManager;
import ch.realmtech.server.level.map.WorldMap;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.ArrayList;
import java.util.List;

@All(InfChunkComponent.class)
public class ChunkRendererSystem extends IteratingSystem {
    @Wire(name = "gameStage")
    private Stage gameStage;
    @Wire
    private TextureAtlas textureAtlas;
    private ComponentMapper<InfMapComponent> mMap;
    private ComponentMapper<InfChunkComponent> mChunk;
    private ComponentMapper<CellComponent> mCell;
    private OrthogonalTiledMapRenderer mapRenderer;

    @Override
    protected void begin() {
        gameStage.getBatch().setProjectionMatrix(gameStage.getCamera().combined);
        gameStage.getCamera().update();
        gameStage.getBatch().begin();
    }
    @Override
    protected void process(int chunkId) {
        InfChunkComponent infChunkComponent = mChunk.get(chunkId);
        List<List<CellComponent>> arrayLayerCell = new ArrayList<>();
        for (int j = 0; j < WorldMap.NUMBER_LAYER; j++) {
            arrayLayerCell.add(new ArrayList<>());
        }
        for (int j = infChunkComponent.infCellsId.length -1; j >= 0; j--) {
            int cellId = infChunkComponent.infCellsId[j];
            CellComponent cellComponent = mCell.get(cellId);
            byte layer = cellComponent.cellRegisterEntry.getCellBehavior().getLayer();
            arrayLayerCell.get(layer).add(cellComponent);
        }
        for (List<CellComponent> cellComponents : arrayLayerCell) {
            if (cellComponents != null) {
                for (CellComponent cellComponent : cellComponents) {
                    int worldX = MapManager.getWorldPos(infChunkComponent.chunkPosX, cellComponent.getInnerPosX());
                    int worldY = MapManager.getWorldPos(infChunkComponent.chunkPosY, cellComponent.getInnerPosY());
                    TextureRegion textureRegion = cellComponent.cellRegisterEntry.getTextureRegion(textureAtlas);
                    gameStage.getBatch().draw(textureRegion, worldX, worldY, textureRegion.getRegionWidth() * RealmTech.UNITE_SCALE, textureRegion.getRegionHeight() * RealmTech.UNITE_SCALE);
                }
            }
        }
    }

    protected void end() {
        gameStage.getBatch().end();
    }
}
