package ch.realmtech.game.level.chunk;

import ch.realmtech.RealmTech;
import ch.realmtech.game.level.cell.CellType;
import ch.realmtech.game.level.cell.GameCell;
import ch.realmtech.game.map.RealmTechTiledMap;
import ch.realmtech.game.worldGeneration.PerlinNoise;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;

public class GameChunk {
    public final static byte CHUNK_SIZE = 16;
    private final RealmTech context;
    private final GameCell cells[][];
    private final int chunkPossX, chunkPossY;
    private final RealmTechTiledMap map;

    public GameChunk(RealmTechTiledMap map, RealmTech context, int chunkPossX, int chunkPossY) {
        this.map = map;
        this.context = context;
        this.chunkPossX = chunkPossX;
        this.chunkPossY = chunkPossY;
        cells = new GameCell[CHUNK_SIZE][CHUNK_SIZE];
    }

    public void generateNewChunk(PerlinNoise perlinNoise) {
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int y = 0; y < CHUNK_SIZE; y++) {
                final int worldX = getWorldPossX(x);
                final int worldY = getWorldPossY(y);
                final CellType cellType;
                if (perlinNoise.getGrid()[worldX][worldY] > 0f && perlinNoise.getGrid()[worldX][worldY] < 0.5f){
                    cellType = CellType.GRASS;
                } else if (perlinNoise.getGrid()[worldX][worldY] >= 0.5f) {
                    cellType = CellType.SAND;
                } else {
                    cellType = CellType.WATER;
                }
                cells[x][y] = new GameCell(this, cellType);
            }
        }
    }

    public GameCell getCell(int relatifChunkPossX, int relatifChunkPossY) {
        return cells[relatifChunkPossX][relatifChunkPossY];
    }

    public void placeChunkOnMap(RealmTechTiledMap map) {
        for (int x = 0; x < GameChunk.CHUNK_SIZE; x++) {
            for (int y = 0; y < GameChunk.CHUNK_SIZE; y++) {
                GameCell gameCell = getCell(x,y);
                map.getLayerTiledLayer(0).setCell((chunkPossX * GameChunk.CHUNK_SIZE) + x, (chunkPossY * GameChunk.CHUNK_SIZE) + y, new TiledMapTileLayer.Cell().setTile(new StaticTiledMapTile(context.getTextureAtlas().findRegion(gameCell.getCellType().textureName))));
            }
        }
    }

    public int getWorldPossX(int cellX) {
        return chunkPossX * GameChunk.CHUNK_SIZE + cellX;
    }

    public int getWorldPossY(int cellY) {
        return chunkPossY * GameChunk.CHUNK_SIZE + cellY;
    }
}
