package ch.realmtech.game.io;

import ch.realmtech.game.level.chunk.GameChunk;
import ch.realmtech.game.level.map.RealmTechTiledMap;

public class SavedWorld {
    private final Save save;
    private GameChunk[][] savedGameChunks;

    public SavedWorld(Save save) {
        this.save = save;
        savedGameChunks = new GameChunk[RealmTechTiledMap.NUMBER_CHUNK_WITH][RealmTechTiledMap.NUMBER_CHUNK_HIGH];
    }

    public void setSavedChunk(GameChunk gameChunk){
        savedGameChunks[gameChunk.getChunkPossX()][gameChunk.getChunkPossY()] = gameChunk;
    }
}
