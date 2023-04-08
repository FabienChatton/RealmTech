package ch.realmtech.game.level.cell;

import ch.realmtech.game.level.chunk.GameChunk;

public class GameCellFactory {

    public static GameCell createbytype(GameChunk chunk, byte innerPossX, byte innerPossY, byte layer, CellType cellType) {
        return new GameCell(chunk, innerPossX, innerPossY, layer, cellType);
    }

    public static GameCell createByType(GameChunk chunk, byte innerPoss, byte layer, CellType cellType) {
        return new GameCell(chunk, innerPoss, layer, cellType);
    }

    public static GameCell createByTypeOnTop(GameChunk gameChunk, byte innerPoss, CellType cellType) {
        byte topLayer = gameChunk.getOnTopLayer(GameCell.getInnerChunkPossX(innerPoss), GameCell.getInnerChunkPossY(innerPoss));
        return createByType(gameChunk, innerPoss, topLayer, cellType);
    }
}
