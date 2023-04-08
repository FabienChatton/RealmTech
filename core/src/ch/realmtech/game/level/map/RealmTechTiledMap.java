package ch.realmtech.game.level.map;

import ch.realmtech.RealmTech;
import ch.realmtech.game.io.Save;
import ch.realmtech.game.level.cell.GameCell;
import ch.realmtech.game.level.chunk.GameChunk;
import ch.realmtech.game.level.chunk.GameChunkFactory;
import ch.realmtech.game.level.worldGeneration.PerlinNoise;
import ch.realmtech.game.level.worldGeneration.PerlineNoise2;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.io.IOException;
import java.util.Random;

public class RealmTechTiledMap {
    public final static int NUMBER_CHUNK_WITH = 10;
    public final static int NUMBER_CHUNK_HIGH = 10;

    public final static int WORLD_WITH = NUMBER_CHUNK_WITH * GameChunk.CHUNK_SIZE;
    public final static int WORLD_HIGH = NUMBER_CHUNK_HIGH * GameChunk.CHUNK_SIZE;
    private final Save save;
    private PerlinNoise perlinNoise;
    private long seed;
    private GameChunk[][] gameChunks = new GameChunk[NUMBER_CHUNK_WITH][NUMBER_CHUNK_HIGH];
    private TiledMap map;

    public RealmTechTiledMap(Save save) {
        this.save = save;
    }

    public void creerMapAleatoire() {
        seed = MathUtils.random(Long.MIN_VALUE, Long.MAX_VALUE - 1);
        System.out.println(seed);
        perlinNoise = new PerlinNoise(new Random(seed), WORLD_WITH, WORLD_HIGH, new PerlineNoise2(7, 0.6f, 0.005f));
        gameChunks = new GameChunk[NUMBER_CHUNK_WITH][NUMBER_CHUNK_HIGH];
        for (int chunkPossX = 0; chunkPossX < NUMBER_CHUNK_WITH; chunkPossX++) {
            for (int chunkPossY = 0; chunkPossY < NUMBER_CHUNK_HIGH; chunkPossY++) {
                gameChunks[chunkPossX][chunkPossY] = GameChunkFactory.generateChunk(this, chunkPossX, chunkPossY, perlinNoise);
            }
        }
        initAndPlace(WORLD_WITH, WORLD_HIGH, 32, 32, GameChunk.NUMBER_LAYER, gameChunks);
    }

    public void placeMap() {
        for (int x = 0; x < gameChunks.length; x++) {
            for (int y = 0; y < gameChunks[x].length; y++) {
                gameChunks[x][y].placeChunkOnMap();
            }
        }
    }


    public TiledMapTileLayer getLayerTiledLayer(int index) {
        return map != null ? (TiledMapTileLayer) map.getLayers().get(index) : null;
    }

    public GameChunk getGameChunk(int worldX, int worldY) {
        int chunkPossX = GameChunk.getChunkPossX(worldX);
        int chunkPossY = GameChunk.getChunkPossY(worldY);
        return gameChunks[chunkPossX][chunkPossY];
    }

    public void setGameChunk(int chunkPossX, int chunkPossY, GameChunk gameChunk) {
        gameChunks[chunkPossX][chunkPossY] = gameChunk;
    }

    private static byte getInnerChunk(int worldPoss) {
        return (byte) (worldPoss % GameChunk.CHUNK_SIZE);
    }

    public GameCell getGameCell(int worldX, int worldY, byte layer) {
        GameChunk gameChunk = getGameChunk(worldX, worldY);
        return gameChunk.getCell(getInnerChunk(worldX), getInnerChunk(worldY), layer);
    }

    public GameCell getGroundCell(int worldX, int worldY) {
        GameChunk gameChunk = getGameChunk(worldX, worldY);
        return gameChunk.getCell(getInnerChunk(worldX), getInnerChunk(worldY), (byte) 0);
    }

    public GameCell getTopCell(int worldX, int worldY) {
        GameChunk gameChunk = getGameChunk(worldX, worldY);
        return gameChunk.getTopCell(getInnerChunk(worldX), getInnerChunk(worldY));
    }

    public void setGroundCell(int worldX, int worldY, GameCell gameCell) {
        GameChunk gameChunk = getGameChunk(worldX, worldY);
        gameChunk.setCell(getInnerChunk(worldX), getInnerChunk(worldY), (byte) 0, gameCell);
    }

    public void setTopCell(int worldX, int worldY, GameCell gameCell) {
        GameChunk gameChunk = getGameChunk(worldX, worldY);
        gameChunk.setTopCell(getInnerChunk(worldX), getInnerChunk(worldY), gameCell);
    }

    public void setOnTopCell(int worldX, int worldY, GameCell gameCell) {
        GameChunk gameChunk = getGameChunk(worldX, worldY);
        gameChunk.setOnTopCell(getInnerChunk(worldX), getInnerChunk(worldY), gameCell);
    }

    public void setCell(int worldX, int worldY, byte layer, GameCell gameCell) {
        GameChunk gameChunk = getGameChunk(worldX, worldY);
        gameChunk.setCell(getInnerChunk(worldX), getInnerChunk(worldY), layer, gameCell);
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

    public void init(int worldWith, int worldHigh, int tileWith, int tileHigh, byte numberLayer) {
        map = new TiledMap();
        for (int i = 0; i < numberLayer; i++) {
            map.getLayers().add(new TiledMapTileLayer(worldWith, worldHigh, tileWith, tileHigh));
        }
        map.getProperties().put("spawn-point", new Vector2(worldWith / 2, worldHigh / 2));
        getContext().getEcsEngine().generateBodyWorldBorder(0, 0, worldWith, worldHigh);
    }

    public void initAndPlace(int worldWith, int worldHigh, int tileWith, int tileHigh, byte numberLayer, GameChunk[][] chunks) {
        init(worldWith, worldHigh, tileWith, tileHigh, numberLayer);
        setGameChunks(chunks);
        placeMap();
        save.getContext().setMapRenderer(map);
    }

    public MapProperties getProperties() {
        return map != null ? map.getProperties() : null;
    }

    public TiledMap getMap() {
        return map;
    }

    public long getSeed() {
        return seed;
    }
}
