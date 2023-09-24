package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import ch.realmtechServer.ecs.component.InfCellComponent;
import ch.realmtechServer.ecs.component.InfChunkComponent;
import ch.realmtechServer.ecs.component.InfMapComponent;
import ch.realmtechServer.ecs.system.MapManager;
import ch.realmtechServer.level.map.WorldMap;
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
    private ComponentMapper<InfCellComponent> mCell;
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
        List<List<InfCellComponent>> arrayLayerCell = new ArrayList<>();
        for (int j = 0; j < WorldMap.NUMBER_LAYER; j++) {
            arrayLayerCell.add(new ArrayList<>());
        }
        for (int j = infChunkComponent.infCellsId.length -1; j >= 0; j--) {
            int cellId = infChunkComponent.infCellsId[j];
            InfCellComponent infCellComponent = mCell.get(cellId);
            byte layer = infCellComponent.cellRegisterEntry.getCellBehavior().getLayer();
            arrayLayerCell.get(layer).add(infCellComponent);
        }
        for (List<InfCellComponent> infCellComponents : arrayLayerCell) {
            if (infCellComponents != null) {
                for (InfCellComponent infCellComponent : infCellComponents) {
                    int worldX = MapManager.getWorldPos(infChunkComponent.chunkPosX, infCellComponent.getInnerPosX());
                    int worldY = MapManager.getWorldPos(infChunkComponent.chunkPosY, infCellComponent.getInnerPosY());
                    TextureRegion textureRegion = infCellComponent.cellRegisterEntry.getTextureRegion(textureAtlas);
                    gameStage.getBatch().draw(textureRegion, worldX, worldY, textureRegion.getRegionWidth() * RealmTech.UNITE_SCALE, textureRegion.getRegionHeight() * RealmTech.UNITE_SCALE);
                }
            }
        }
    }

    protected void end() {
        gameStage.getBatch().end();
    }
}
