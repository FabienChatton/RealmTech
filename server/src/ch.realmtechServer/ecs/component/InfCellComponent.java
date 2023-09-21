package ch.realmtechServer.ecs.component;

import ch.realmtechServer.level.cell.Cells;
import ch.realmtechServer.registery.CellRegisterEntry;
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
