package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.component.CellComponent;
import ch.realmtech.game.ecs.component.ChunkComponent;
import ch.realmtech.game.ecs.component.WorldMapComponent;
import ch.realmtech.game.level.cell.Cells;
import ch.realmtech.game.level.map.WorldMap;
import ch.realmtech.game.level.worldGeneration.PerlinNoise;
import ch.realmtech.game.level.worldGeneration.PerlineNoise2;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.EntitySubscription;
import com.artemis.EntitySystem;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Random;

@All(WorldMapComponent.class)
public class WorldMapManager extends EntitySystem {

    @Wire(name = "context")
    RealmTech context;

    int worldMapId;

    private ComponentMapper<WorldMapComponent> mWorldMap;
    @Override
    protected void processSystem() {

    }


    public void init(int worldMapId, int worldWith, int worldHigh, int tileWith, int tileHigh, byte numberLayer) {
        this.worldMapId = worldMapId;
        initMap(worldWith, worldHigh, tileWith, tileHigh, numberLayer);
    }

    public void initMap(int worldWith, int worldHigh, int tileWith, int tileHigh, byte numberLayer) {
        final WorldMapComponent worldMap = mWorldMap.get(worldMapId);
        worldMap.worldMap = new WorldMap();
        for (int i = 0; i < numberLayer; i++) {
            worldMap.worldMap.getLayers().add(new TiledMapTileLayer(worldWith, worldHigh, tileWith, tileHigh));
        }
        worldMap.worldMap.getProperties().put("spawn-point", new Vector2(worldWith / 2, worldHigh / 2));
        context.getEcsEngine().generateBodyWorldBorder(0, 0, worldWith, worldHigh);
    }

    public void generateNewWorldMap(int worldMapId) {
        final WorldMapComponent worldMap = mWorldMap.get(worldMapId);
        worldMap.seed = MathUtils.random(Long.MIN_VALUE, Long.MAX_VALUE - 1);
        worldMap.perlinNoise = new PerlinNoise(new Random(worldMap.seed), WorldMap.WORLD_WITH, WorldMap.WORLD_HIGH, new PerlineNoise2(7, 0.6f, 0.005f));
        world.getSystem(ChunkManager.class).genereteNewChunks(worldMapId, worldMap.perlinNoise);
        placeWorldMap(worldMapId);
    }

    public void placeWorldMap(int worldMapId) {
        world.getSystem(WorldMapRendererSystem.class).setMapRenderer(mWorldMap.get(worldMapId).worldMap);
    }

    public void placeOnMap(int worldPossX, int worldPossY, byte layer, TiledMapTileLayer.Cell cell) {
        ((TiledMapTileLayer) mWorldMap.get(worldMapId).worldMap.getLayers().get(layer)).setCell(worldPossX, worldPossY, cell);
    }

    public long getSeed() {
        return mWorldMap.get(worldMapId).seed;
    }

    public void saveWorldMap(OutputStream outputStream) throws IOException {
        CellManager cellManager = world.getSystem(CellManager.class);
        EntitySubscription chunkSubscription = world.getAspectSubscriptionManager()
                .get(Aspect.all(ChunkComponent.class));
        EntitySubscription cellSubscription = world.getAspectSubscriptionManager()
                .get(Aspect.all(CellComponent.class));

        IntBag chunkBag = chunkSubscription.getEntities();
        IntBag cellBag = cellSubscription.getEntities();

        ComponentMapper<ChunkComponent> mChunk = world.getMapper(ChunkComponent.class);
        ComponentMapper<CellComponent> mCell = world.getMapper(CellComponent.class);

        for (int chunkId : chunkBag.getData()) {
            ChunkComponent chunkComponent = mChunk.get(chunkId);
            if (chunkComponent != null) {
                int chunkPossX = chunkComponent.chunkPossX;
                int chunkPossY = chunkComponent.chunkPossY;
                outputStream.write(ByteBuffer.allocate(Integer.BYTES).putInt(chunkPossX).array());
                outputStream.write(ByteBuffer.allocate(Integer.BYTES).putInt(chunkPossY).array());
                short nombreDeCelluleDansCeChunk = 0;
                for (int cellId : cellBag.getData()) {
                    CellComponent cellComponent = mCell.get(cellId);
                    if (cellComponent != null) {
                        if (cellComponent.parentChunk == chunkId) {
                            nombreDeCelluleDansCeChunk++;
                        }
                    }
                }
                outputStream.write(ByteBuffer.allocate(Short.BYTES).putShort(nombreDeCelluleDansCeChunk).array());
                for (int cellId : cellBag.getData()) {
                    CellComponent cellComponent = mCell.get(cellId);
                    if (cellComponent != null && cellComponent.parentChunk == chunkId) {
                        outputStream.write(ByteBuffer.allocate(Integer.BYTES).putInt(cellManager.getModAndCellHash(cellComponent.cellRegisterEntry)).array());
                        outputStream.write(Cells.getInnerChunkPoss(cellComponent.innerChunkPossX, cellComponent.innerChunkPossY));
                        outputStream.write(cellComponent.layer);
                    }
                }
            }
        }
    }

    public int getWorldX(int chunkPossX, byte innerChunkPossX) {
        return chunkPossX * WorldMap.CHUNK_SIZE + innerChunkPossX;
    }

    public int getWorldY(int chunkPossY, byte innerChunkPossY) {
        return chunkPossY * WorldMap.CHUNK_SIZE + innerChunkPossY;
    }
}
