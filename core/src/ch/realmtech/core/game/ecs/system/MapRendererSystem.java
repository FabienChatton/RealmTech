package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.RealmTech;
import ch.realmtech.server.ecs.component.CellComponent;
import ch.realmtech.server.ecs.component.FaceComponent;
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
    private ComponentMapper<CellComponent> mCell;
    private ComponentMapper<FaceComponent> mFace;

    @Override
    protected void begin() {
        gameStage.getBatch().begin();
    }

    @Override
    protected void process(int mapId) {
        InfMapComponent infMapComponent = mMap.get(mapId);
        List<List<List<Integer>>> chunks = getInOrder(infMapComponent);
        draw(chunks, infMapComponent);
    }

    private void draw(List<List<List<Integer>>> chunks, InfMapComponent infMapComponent) {
        for (int i = chunks.size() - 1; i >= 0; i--) {
            for (List<Integer> cellComponents : chunks.get(i)) {
                if (cellComponents != null) {
                    for (Integer cellId : cellComponents) {
                        InfChunkComponent infChunkComponent = mChunk.get(infMapComponent.infChunks[i]);
                        CellComponent cellComponent = mCell.get(cellId);
                        int worldX = MapManager.getWorldPos(infChunkComponent.chunkPosX, cellComponent.getInnerPosX());
                        int worldY = MapManager.getWorldPos(infChunkComponent.chunkPosY, cellComponent.getInnerPosY());
                        TextureRegion textureRegion;
                        if (mFace.has(cellId)) {
                            textureRegion = textureAtlas.findRegion(mFace.get(cellId).getFaceTexture());
                        } else {
                            textureRegion = cellComponent.cellRegisterEntry.getTextureRegion(textureAtlas);
                        }
                        if (textureRegion == null) {
                            textureRegion = textureAtlas.findRegion("default-texture");
                        }
                        gameStage.getBatch().draw(textureRegion, worldX, worldY, textureRegion.getRegionWidth() * RealmTech.UNITE_SCALE, textureRegion.getRegionHeight() * RealmTech.UNITE_SCALE);
                    }
                }
            }
        }
    }

    private List<List<List<Integer>>> getInOrder(InfMapComponent infMapComponent) {
        List<List<List<Integer>>> chunks = new ArrayList<>();
        for (int i = 0; i < infMapComponent.infChunks.length; i++) {
            int chunkId = infMapComponent.infChunks[i];
            InfChunkComponent infChunkComponent = mChunk.get(chunkId);
            List<List<Integer>> arrayLayerCell = new ArrayList<>();
            for (int j = 0; j < WorldMap.NUMBER_LAYER; j++) {
                arrayLayerCell.add(new ArrayList<>());
            }
            for (int j = infChunkComponent.infCellsId.length -1; j >= 0; j--) {
                int cellId = infChunkComponent.infCellsId[j];
                CellComponent cellComponent = mCell.get(cellId);
                byte layer = cellComponent.cellRegisterEntry.getCellBehavior().getLayer();
                arrayLayerCell.get(layer).add(cellId);
            }
            chunks.add(arrayLayerCell);
        }
        return chunks;
    }

    @Override
    protected void end() {
        gameStage.getBatch().end();
    }
}
