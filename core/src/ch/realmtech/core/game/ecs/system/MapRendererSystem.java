package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.RealmTech;
import ch.realmtech.server.ecs.component.InfCellComponent;
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

@All(InfMapComponent.class)
public class MapRendererSystem extends IteratingSystem {
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
        gameStage.getBatch().begin();
    }

    @Override
    protected void process(int mapId) {
        InfMapComponent infMapComponent = mMap.get(mapId);
        List<List<List<InfCellComponent>>> chunks = new ArrayList<>();
        for (int i = 0; i < infMapComponent.infChunks.length; i++) {
            int chunkId = infMapComponent.infChunks[i];
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
            chunks.add(arrayLayerCell);
        }
        for (int i = chunks.size() - 1; i >= 0; i--) {
            for (List<InfCellComponent> infCellComponents : chunks.get(i)) {
                if (infCellComponents != null) {
                    for (InfCellComponent infCellComponent : infCellComponents) {
                        InfChunkComponent infChunkComponent = mChunk.get(infMapComponent.infChunks[i]);
                        int worldX = MapManager.getWorldPos(infChunkComponent.chunkPosX, infCellComponent.getInnerPosX());
                        int worldY = MapManager.getWorldPos(infChunkComponent.chunkPosY, infCellComponent.getInnerPosY());
                        TextureRegion textureRegion = infCellComponent.cellRegisterEntry.getTextureRegion(textureAtlas);
                        gameStage.getBatch().draw(textureRegion, worldX, worldY, textureRegion.getRegionWidth() * RealmTech.UNITE_SCALE, textureRegion.getRegionHeight() * RealmTech.UNITE_SCALE);
                    }
                }
            }
        }
    }

    @Override
    protected void end() {
        gameStage.getBatch().end();
    }
}