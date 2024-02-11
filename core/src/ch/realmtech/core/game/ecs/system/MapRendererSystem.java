package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.RealmTech;
import ch.realmtech.core.game.ecs.plugin.SystemsAdminClient;
import ch.realmtech.server.ecs.component.CellComponent;
import ch.realmtech.server.ecs.component.FaceComponent;
import ch.realmtech.server.ecs.component.InfChunkComponent;
import ch.realmtech.server.ecs.component.InfMapComponent;
import ch.realmtech.server.ecs.system.MapManager;
import ch.realmtech.server.level.cell.CellBehavior;
import ch.realmtech.server.level.map.WorldMap;
import ch.realmtech.server.mod.RealmTechCoreMod;
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
    private SystemsAdminClient systemsAdminClient;
    private ComponentMapper<InfMapComponent> mMap;
    private ComponentMapper<InfChunkComponent> mChunk;
    private ComponentMapper<CellComponent> mCell;
    private ComponentMapper<FaceComponent> mFace;

    private final static byte[][] ROTATION_MATRIX = {
            {0, 2, 1, 3, 2, 2, 1, 3, 0, 1, 2, 3, 1, 0, 1, 2},
            {2, 1, 0, 2, 1, 3, 0, 2, 1, 3, 0, 1, 2, 1, 2, 3},
            {1, 3, 1, 3, 0, 0, 2, 1, 3, 2, 3, 1, 0, 2, 0, 1},
            {3, 0, 2, 1, 2, 1, 3, 0, 1, 1, 2, 3, 1, 3, 1, 0},
            {2, 1, 3, 0, 1, 0, 2, 3, 2, 0, 3, 2, 0, 2, 3, 2},
            {1, 3, 0, 2, 3, 2, 0, 1, 0, 1, 2, 3, 1, 0, 1, 2},
            {0, 3, 3, 1, 2, 0, 1, 2, 2, 3, 1, 2, 1, 3, 3, 3},
            {3, 0, 3, 0, 3, 3, 0, 3, 0, 0, 1, 0, 2, 2, 3, 3},
            {3, 1, 0, 1, 3, 2, 2, 1, 3, 1, 3, 2, 1, 0, 2, 1},
            {3, 1, 1, 1, 2, 1, 2, 0, 3, 0, 2, 1, 2, 0, 3, 1},
            {3, 1, 0, 3, 2, 1, 0, 3, 0, 3, 0, 2, 0, 2, 2, 0},
            {3, 0, 2, 2, 3, 2, 1, 1, 2, 3, 1, 0, 3, 1, 3, 1},
            {2, 0, 1, 3, 1, 1, 2, 2, 3, 3, 0, 2, 1, 3, 2, 3},
            {1, 2, 3, 3, 0, 0, 3, 0, 2, 3, 1, 0, 2, 2, 0, 2},
            {3, 0, 2, 2, 3, 2, 1, 1, 2, 3, 1, 0, 3, 1, 3, 1},
            {2, 2, 3, 0, 0, 0, 1, 1, 3, 0, 0, 2, 3, 1, 1, 2},
    };

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
                        if (cellComponent.cellRegisterEntry == RealmTechCoreMod.GRASS_CELL) {
                            byte innerChunkX = MapManager.getInnerChunk(worldX);
                            byte innerChunkY = MapManager.getInnerChunk(worldY);
                            int rotation = ROTATION_MATRIX[innerChunkX][innerChunkY];
                            CellBehavior grassCellBehavior = RealmTechCoreMod.GRASS_CELL.getCellBehavior();
                            String tiledTextureRegionName = RealmTechCoreMod.GRASS_CELL.getTextureRegionName()
                                    + "-"
                                    + (Math.abs(worldX) % (grassCellBehavior.getTiledTextureX() + 1))
                                    + "-"
                                    + (Math.abs(worldY) % (grassCellBehavior.getTiledTextureY() + 1));
                            textureRegion = textureAtlas.findRegion(tiledTextureRegionName);
                            gameStage.getBatch().draw(
                                    textureRegion,
                                    worldX,
                                    worldY,
                                    0.5f,
                                    0.5f,
                                    textureRegion.getRegionWidth() * RealmTech.UNITE_SCALE,
                                    textureRegion.getRegionHeight() * RealmTech.UNITE_SCALE,
                                    1,
                                    1,
                                    90 * rotation
                            );
                        } else {
                            gameStage.getBatch().draw(
                                    textureRegion,
                                    worldX,
                                    worldY,
                                    textureRegion.getRegionWidth() * RealmTech.UNITE_SCALE,
                                    textureRegion.getRegionHeight() * RealmTech.UNITE_SCALE
                            );
                        }
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
