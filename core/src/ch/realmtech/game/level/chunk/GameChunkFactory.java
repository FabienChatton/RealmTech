package ch.realmtech.game.level.chunk;

import ch.realmtech.game.level.cell.GameCell;
import ch.realmtech.game.level.map.RealmTechTiledMap;
import ch.realmtech.game.level.worldGeneration.PerlinNoise;

public class GameChunkFactory {
    public static GameChunk generateChunk(RealmTechTiledMap map, int chunkPossX, int chunkPossY, PerlinNoise perlinNoise) {
        return new GameChunk(map, chunkPossX, chunkPossY, perlinNoise);
    }

    public static GameChunk createAndFillChunk(RealmTechTiledMap map, int chunkPossX, int chunkPossY, GameCell[][] cells) {
        return new GameChunk(map, chunkPossX, chunkPossY, cells);
    }

    public static GameChunk createEmptyChunk(RealmTechTiledMap map, int chunkPossX, int chunkPosY) {
        return new GameChunk(map, chunkPossX, chunkPosY, new GameCell[GameChunk.CHUNK_SIZE][GameChunk.CHUNK_SIZE]);
    }
}
