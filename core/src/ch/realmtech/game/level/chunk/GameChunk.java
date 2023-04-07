package ch.realmtech.game.level.chunk;

import ch.realmtech.game.io.Save;
import ch.realmtech.game.level.cell.CellType;
import ch.realmtech.game.level.cell.GameCell;
import ch.realmtech.game.level.map.RealmTechTiledMap;
import ch.realmtech.game.level.worldGeneration.PerlinNoise;

import java.io.IOException;

public class GameChunk {
    public final static byte CHUNK_SIZE = 16;
    private final GameCell[][] cells;
    private final int chunkPossX, chunkPossY;
    private final RealmTechTiledMap map;

    GameChunk(RealmTechTiledMap map, int chunkPossX, int chunkPossY, PerlinNoise perlinNoise) {
        this.map = map;
        this.chunkPossX = chunkPossX;
        this.chunkPossY = chunkPossY;
        this.cells = generateCellNewChunk(perlinNoise);
//        cells = new GameCell[CHUNK_SIZE][CHUNK_SIZE];
//        for (byte x = 0; x < CHUNK_SIZE; x++) {
//            for (byte y = 0; y < CHUNK_SIZE; y++) {
//                final int worldX = getWorldPossX(x);
//                final int worldY = getWorldPossY(y);
//                final CellType cellType;
//                if (perlinNoise.getGrid()[worldX][worldY] > 0f && perlinNoise.getGrid()[worldX][worldY] < 0.5f){
//                    cellType = CellType.GRASS;
//                } else if (perlinNoise.getGrid()[worldX][worldY] >= 0.5f) {
//                    cellType = CellType.SAND;
//                } else {
//                    cellType = CellType.WATER;
//                }
//                cells[x][y] = new GameCell(this, x, y, cellType);
//            }
//        }
    }

    GameChunk(RealmTechTiledMap map, int chunkPossX, int chunkPossY, GameCell[][] gameCells) {
        this.map = map;
        this.chunkPossX = chunkPossX;
        this.chunkPossY = chunkPossY;
        this.cells = gameCells;
    }

    private GameCell[][] generateCellNewChunk(PerlinNoise perlinNoise) {
        final GameCell[][] gameCells = new GameCell[CHUNK_SIZE][CHUNK_SIZE];
        for (byte x = 0; x < CHUNK_SIZE; x++) {
            for (byte y = 0; y < CHUNK_SIZE; y++) {
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
                gameCells[x][y] = new GameCell(this, x, y, cellType);
            }
        }
        return gameCells;
    }

    public void placeChunkOnMap() {
        for (int x = 0; x < GameChunk.CHUNK_SIZE; x++) {
            for (int y = 0; y < GameChunk.CHUNK_SIZE; y++) {
                GameCell gameCell = cells[x][y];
                if (gameCell != null) {
                    gameCell.placeCellOnMap(0);
                } else {
                    map.getLayerTiledLayer(0).setCell(getWorldPossX(x),getWorldPossY(y),null);
                }
            }
        }
    }

    public int getWorldPossX(int innerChunkX) {
        return getWorldPossX(chunkPossX, innerChunkX);
    }

    public static int getWorldPossX(int chunkPossX, int innerChunkX) {
        return chunkPossX * GameChunk.CHUNK_SIZE + innerChunkX;
    }

    public int getWorldPossY(int innerChunkY) {
        return getWorldPossY(chunkPossY, innerChunkY);
    }

    public static int getWorldPossY(int chunkPossY, int innerChunkY) {
        return chunkPossY * GameChunk.CHUNK_SIZE + innerChunkY;
    }

    public GameCell getCell(int innerChunkX, int innerChunkY) {
        return cells[innerChunkX][innerChunkY];
    }

    public void setCell(int innerChunkX, int innerChunkY, GameCell gameCell) {
        cells[innerChunkX][innerChunkY] = gameCell;
        if (gameCell == null) {
            map.getLayerTiledLayer(0).setCell(getWorldPossX(innerChunkX), getWorldPossY(innerChunkY), null);
        } else {
            gameCell.placeCellOnMap(0);
        }
    }

    public void saveChunk(final Save save) throws IOException {
        save.write(chunkPossX);
        save.write(chunkPossY);
        for (byte innerChunkPossX = 0; innerChunkPossX < GameChunk.CHUNK_SIZE; innerChunkPossX++) {
            for (byte innerChunkPossY = 0; innerChunkPossY < GameChunk.CHUNK_SIZE; innerChunkPossY++) {
                final GameCell gameCell = cells[innerChunkPossX][innerChunkPossY];
                if (gameCell != null) {
                    gameCell.write(save);
                } else {
                    save.write(CellType.getIdCellType(null));
                    save.write(GameCell.getInnerChunkPoss(innerChunkPossX, innerChunkPossY));
                }
            }
        }
    }

    public int getChunkPossX() {
        return chunkPossX;
    }

    public int getChunkPossY() {
        return chunkPossY;
    }

    public RealmTechTiledMap getMap() {
        return map;
    }

    public void setCells(GameCell[][] gameCells) {
        for (int x = 0; x < GameChunk.CHUNK_SIZE; x++) {
            for (int y = 0; y < GameChunk.CHUNK_SIZE; y++) {
                cells[x][y] = gameCells[x][y];
            }
        }
    }
}
