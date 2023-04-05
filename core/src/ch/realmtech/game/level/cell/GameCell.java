package ch.realmtech.game.level.cell;

import ch.realmtech.game.level.chunk.GameChunk;

public class GameCell {
    private final CellType cellType;
    private final GameChunk chunk;

    public GameCell(GameChunk chunk, CellType cellType) {
        this.cellType = cellType;
        this.chunk = chunk;
    }

    public CellType getCellType() {
        return cellType;
    }
}
