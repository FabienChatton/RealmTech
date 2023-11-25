package ch.realmtech.server.ecs.component;

import ch.realmtech.server.level.cell.Cells;
import ch.realmtech.server.registery.CellRegisterEntry;
import com.artemis.PooledComponent;

import java.nio.ByteBuffer;

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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof InfCellComponent testInfCellComponent) {
            return innerPos == testInfCellComponent.innerPos && cellRegisterEntry == testInfCellComponent.cellRegisterEntry;
        }
        return false;
    }
}
