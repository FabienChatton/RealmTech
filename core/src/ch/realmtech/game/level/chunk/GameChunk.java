package ch.realmtech.game.level.chunk;

import ch.realmtech.game.io.Save;
import ch.realmtech.game.level.cell.CellType;
import ch.realmtech.game.level.cell.GameCell;
import ch.realmtech.game.level.cell.GameCellFactory;
import ch.realmtech.game.level.map.RealmTechTiledMap;
import ch.realmtech.game.level.worldGeneration.PerlinNoise;

import java.io.IOException;

public class GameChunk {
    public final static byte CHUNK_SIZE = 16;
    public final static byte NUMBER_LAYER = 4;
    private GameCell[][][] cells;
    private final int chunkPossX, chunkPossY;
    private final RealmTechTiledMap map;

    /** générer un nouveau chunk */
    GameChunk(RealmTechTiledMap map, int chunkPossX, int chunkPossY, PerlinNoise perlinNoise) {
        this.map = map;
        this.chunkPossX = chunkPossX;
        this.chunkPossY = chunkPossY;
        this.cells = generateGroundCellsNewChunk(perlinNoise);
    }

    GameChunk(RealmTechTiledMap map, int chunkPossX, int chunkPossY, GameCell[][][] gameCells) {
        this.map = map;
        this.chunkPossX = chunkPossX;
        this.chunkPossY = chunkPossY;
        this.cells = gameCells;
    }

    private GameCell[][][] generateGroundCellsNewChunk(PerlinNoise perlinNoise) {
        final GameCell[][][] gameCells = new GameCell[CHUNK_SIZE][CHUNK_SIZE][NUMBER_LAYER];
        for (byte x = 0; x < CHUNK_SIZE; x++) {
            for (byte y = 0; y < CHUNK_SIZE; y++) {
                final int worldX = getWorldPossX(x);
                final int worldY = getWorldPossY(y);
                final CellType cellType;
                if (perlinNoise.getGrid()[worldX][worldY] > 0f && perlinNoise.getGrid()[worldX][worldY] < 0.5f) {
                    cellType = CellType.GRASS;
                } else if (perlinNoise.getGrid()[worldX][worldY] >= 0.5f) {
                    cellType = CellType.SAND;
                } else {
                    cellType = CellType.WATER;
                }
                gameCells[x][y][0] = GameCellFactory.createbytype(this, x, y, (byte) 0, cellType);
            }
        }
        return gameCells;
    }

    public void placeChunkOnMap() {
        for (byte x = 0; x < GameChunk.CHUNK_SIZE; x++) {
            for (byte y = 0; y < GameChunk.CHUNK_SIZE; y++) {
                for (byte z = 0; z < GameChunk.NUMBER_LAYER; z++) {
                    GameCell gameCell = cells[x][y][z];
                    if (gameCell != null) {
                        gameCell.placeCellOnMap(z);
                    } else {
                        map.getLayerTiledLayer(z).setCell(getWorldPossX(x), getWorldPossY(y), null);
                    }
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

    public GameCell getCell(byte innerChunkX, byte innerChunkY, byte layer) {
        return cells[innerChunkX][innerChunkY][layer];
    }

    public void setCell(byte innerChunkX, byte innerChunkY, byte layer, GameCell gameCell) {
        cells[innerChunkX][innerChunkY][layer] = gameCell;
        if (gameCell == null) {
            map.getLayerTiledLayer(layer).setCell(getWorldPossX(innerChunkX), getWorldPossY(innerChunkY), null);
        } else {
            gameCell.placeCellOnMap(layer);
        }
    }

    public void setCell(GameCell cell) {
        cells[cell.getInnerChunkPossX()][cell.getInnerChunkPossY()][cell.getLayer()] = cell;
        cell.placeCellOnMap(cell.getLayer());
    }

    public void saveChunk(final Save save) throws IOException {
        save.write(chunkPossX);
        save.write(chunkPossY);
        for (byte x = 0; x < GameChunk.CHUNK_SIZE; x++) {
            for (byte y = 0; y < GameChunk.CHUNK_SIZE; y++) {
                for (byte z = 0; z < GameChunk.NUMBER_LAYER; z++) {
                    save.write(GameCell.write(cells[x][y][z]));
                }
            }
        }
    }

    public int getChunkPossX() {
        return chunkPossX;
    }

    public static int getChunkPossX(int worldX) {
        return worldX / GameChunk.CHUNK_SIZE;
    }

    public int getChunkPossY() {
        return chunkPossY;
    }

    public static int getChunkPossY(int worldY) {
        return worldY / GameChunk.CHUNK_SIZE;
    }

    public RealmTechTiledMap getMap() {
        return map;
    }

    public void setCells(GameCell[][][] gameCells) {
        this.cells = gameCells;
    }

    public GameCell getTopCell(int innerChunkX, int innerChunkY) {
        GameCell ret = null;
        for (byte layer = NUMBER_LAYER - 1; layer >= 0; layer--) {
            GameCell cell = cells[innerChunkX][innerChunkY][layer];
            if (cell != null) {
                ret = cell;
                break;
            }
        }
        return ret;
    }

    public byte getTopLayer(byte innerChunkPossX, byte innerChunkPossY) {
        GameCell topCell = getTopCell(innerChunkPossX, innerChunkPossY);
        return topCell == null ? 0 : topCell.getLayer();
    }

    public byte getOnTopLayer(byte innerChunkX, byte innerChunkY) {
        GameCell topCell = getTopCell(innerChunkX, innerChunkY);
        return (byte) (topCell == null ? 0 : topCell.getLayer() + 1);
    }

    public void setTopCell(byte innerChunkX, byte innerChunkY, GameCell gameCell) {
        setCell(innerChunkX, innerChunkY, getTopLayer(innerChunkX,innerChunkY), gameCell);
    }

    public void setOnTopCell(byte innerChunkX, byte innerChunkY, GameCell gameCell) {
        setCell(innerChunkX, innerChunkY, getOnTopLayer(innerChunkX,innerChunkY), gameCell);
    }
}
