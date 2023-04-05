package ch.realmtech.game.map;

import ch.realmtech.RealmTech;
import ch.realmtech.game.level.chunk.GameChunk;
import ch.realmtech.game.worldGeneration.PerlinNoise;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

public class RealmTechTiledMap extends TiledMap {
    public final static int NUMBER_CHUNK_WITH = 10;
    public final static int NUMBER_CHUNK_HIGH = 10;

    public final static int WORLD_WITH = NUMBER_CHUNK_WITH * GameChunk.CHUNK_SIZE;
    public final static int WORLD_HIGH = NUMBER_CHUNK_HIGH * GameChunk.CHUNK_SIZE;
    private final RealmTech context;
    private PerlinNoise perlinNoise;

    public RealmTechTiledMap(RealmTech context) {
        this.context = context;
//        perlinNoise = new PerlinNoise(new Random(), 1, MAP_WIDTH, MAP_HEIGHT);
//        perlinNoise.initialise();
    }

    public void creerMapAleatoire() {
        perlinNoise = new PerlinNoise(new Random(), 1, WORLD_WITH, WORLD_HIGH);
        perlinNoise.initialise();
        getProperties().put("spawn-point", new Vector2(WORLD_WITH / 2, WORLD_HIGH / 2));
        context.getEcsEngine().generateBodyWorldBorder(0, 0, WORLD_WITH, WORLD_HIGH);
        getLayers().add(new TiledMapTileLayer(WORLD_WITH, WORLD_HIGH, 32, 32));

//        final GameChunk[][] gameChunks = new GameChunk[NUMBER_CHUNK_WITH][NUMBER_CHUNK_HIGH];
        for (int chunkPossX = 0; chunkPossX < NUMBER_CHUNK_WITH; chunkPossX++) {
            for (int chunkPossY = 0; chunkPossY < NUMBER_CHUNK_HIGH; chunkPossY++) {
                final GameChunk gameChunk = new GameChunk(this, context, chunkPossX, chunkPossY);
                gameChunk.generateNewChunk(perlinNoise);
                gameChunk.placeChunkOnMap(this);
//                gameChunks[chunkPossX][chunkPossY] = gameChunk;
            }
        }
    }


    public TiledMapTileLayer getLayerTiledLayer(int index) {
        return (TiledMapTileLayer) super.getLayers().get(index);
    }

    public TiledMapTileLayer getLayerTiledLayer(String name) {
        return (TiledMapTileLayer) super.getLayers().get(name);
    }
}
