package ch.realmtech.game.level.cell;

import ch.realmtech.game.level.chunk.GameChunk;

public class GameCellFactory {

    public static GameCell createByteType(GameChunk chunk, byte innerPossX, byte innerPossY, CellType cellType) {
        return new GameCell(chunk, innerPossX, innerPossY, cellType);
    }

    public static GameCell createByteType(GameChunk chunk, byte innerPoss, CellType cellType) {
        return new GameCell(chunk, innerPoss, cellType);
    }
}
