package ch.realmtech.game.level.map;

import ch.realmtech.RealmTech;
import ch.realmtech.game.io.Save;
import ch.realmtech.game.level.cell.GameCell;
import ch.realmtech.game.level.chunk.GameChunk;
import ch.realmtech.game.level.chunk.GameChunkFactory;
import ch.realmtech.game.level.worldGeneration.PerlinNoise;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;

import java.io.IOException;
import java.util.Random;

public class RealmTechTiledMap extends TiledMap {
    public final static int NUMBER_CHUNK_WITH = 10;
    public final static int NUMBER_CHUNK_HIGH = 10;

    public final static int WORLD_WITH = NUMBER_CHUNK_WITH * GameChunk.CHUNK_SIZE;
    public final static int WORLD_HIGH = NUMBER_CHUNK_HIGH * GameChunk.CHUNK_SIZE;
    private final Save save;
    private PerlinNoise perlinNoise;
    private GameChunk[][] gameChunks = new GameChunk[NUMBER_CHUNK_WITH][NUMBER_CHUNK_HIGH];

    public RealmTechTiledMap(Save save) {
        this.save = save;
//        perlinNoise = new PerlinNoise(new Random(), 1, MAP_WIDTH, MAP_HEIGHT);
//        perlinNoise.initialise();
    }

    public void creerMapAleatoire() {
        perlinNoise = new PerlinNoise(new Random(), 1, WORLD_WITH, WORLD_HIGH);
        perlinNoise.initialise();
        final GameChunk[][] chunks = new GameChunk[NUMBER_CHUNK_WITH][NUMBER_CHUNK_HIGH];
        for (int chunkPossX = 0; chunkPossX < NUMBER_CHUNK_WITH; chunkPossX++) {
            for (int chunkPossY = 0; chunkPossY < NUMBER_CHUNK_HIGH; chunkPossY++) {
                chunks[chunkPossX][chunkPossY] = GameChunkFactory.generateChunk(this, chunkPossX, chunkPossY, perlinNoise);
            }
        }
        initAndPlace(WORLD_WITH, WORLD_HIGH, 32,32, chunks);
    }

    public void placeMap() {
        for (int x = 0; x < gameChunks.length; x++) {
            for (int y = 0; y < gameChunks[x].length; y++) {
                gameChunks[x][y].placeChunkOnMap();
            }
        }
    }


    public TiledMapTileLayer getLayerTiledLayer(int index) {
        return (TiledMapTileLayer) super.getLayers().get(index);
    }

    public TiledMapTileLayer getLayerTiledLayer(String name) {
        return (TiledMapTileLayer) super.getLayers().get(name);
    }

    public GameChunk getGameChunk(int worldX, int worldY) {
        int chunkPossX = worldX / GameChunk.CHUNK_SIZE;
        int chunkPossY = worldY / GameChunk.CHUNK_SIZE;
        return gameChunks[chunkPossX][chunkPossY];
    }

    public void setGameChunk(int chunkPossX, int chunkPossY, GameChunk gameChunk) {
        gameChunks[chunkPossX][chunkPossY] = gameChunk;
    }

    public GameCell getGameCell(int worldX, int worldY) {
        GameChunk gameChunk = getGameChunk(worldX, worldY);
        int innerChunkX = worldX % GameChunk.CHUNK_SIZE;
        int innerChunkY = worldY % GameChunk.CHUNK_SIZE;
        return gameChunk.getCell(innerChunkX,innerChunkY);
    }

    public void setCell(int worldX, int worldY, GameCell gameCell) {
        GameChunk gameChunk = getGameChunk(worldX, worldY);
        int innerChunkX = worldX % GameChunk.CHUNK_SIZE;
        int innerChunkY = worldY % GameChunk.CHUNK_SIZE;
        gameChunk.setCell(innerChunkX,innerChunkY, gameCell);

    }

    public void save(final Save save) throws IOException {
        for (int x = 0; x < NUMBER_CHUNK_WITH; x++) {
            for (int y = 0; y < NUMBER_CHUNK_HIGH; y++) {
                final GameChunk gameChunk = gameChunks[x][y];
                if (gameChunk != null) {
                    gameChunk.saveChunk(save);
                }
            }
        }
    }

    public RealmTech getContext() {
        return save.getContext();
    }

    public void setGameChunks(GameChunk[][] chunks) {
        gameChunks = chunks;
    }

    public void init(int worldWith, int worldHigh, int tileWith, int tileHigh) {
        getLayers().add(new TiledMapTileLayer(worldWith, worldHigh, tileWith,tileHigh));
        getProperties().put("spawn-point", new Vector2(worldWith / 2, worldHigh / 2));
        getContext().getEcsEngine().generateBodyWorldBorder(0,0, worldWith, worldHigh);
    }

    public void initAndPlace(int worldWith, int worldHigh, int tileWith, int tileHigh, GameChunk[][] chunks) {
        init(worldWith, worldHigh, tileWith, tileHigh);
        setGameChunks(chunks);
        placeMap();
    }
}
