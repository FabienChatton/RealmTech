package ch.realmtech.game.ecs.component;

import ch.realmtech.game.level.cell.Cells;
import ch.realmtech.game.registery.CellRegisterEntry;
import com.artemis.PooledComponent;

public class InfCellComponent extends PooledComponent {
    private byte innerPos;
    public CellRegisterEntry cellRegisterEntry;

    public InfCellComponent set(byte innerPosX, byte innerPosY, CellRegisterEntry cellRegisterEntry) {
        innerPos = Cells.getInnerChunkPos(innerPosX, innerPosY);
        this.cellRegisterEntry = cellRegisterEntry;
        return this;
    }

    @Override
    protected void reset() {
        innerPos = 0;
    }

    public byte getInnerPosX() {
        return Cells.getInnerChunkPosX(innerPos);
    }

    public byte getInnerPosY() {
        return Cells.getInnerChunkPosY(innerPos);
    }
}
