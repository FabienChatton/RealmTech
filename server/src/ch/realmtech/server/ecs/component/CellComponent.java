package ch.realmtech.server.ecs.component;

import ch.realmtech.server.level.cell.Cells;
import ch.realmtech.server.newRegistry.NewCellEntry;
import com.artemis.PooledComponent;
import com.artemis.annotations.EntityId;

public class CellComponent extends PooledComponent {
    private byte innerPos;
    public NewCellEntry cellRegisterEntry;
    @EntityId
    public int chunkId;

    public CellComponent set(byte innerPosX, byte innerPosY, NewCellEntry cellRegisterEntry, int chunkId) {
        innerPos = Cells.getInnerChunkPos(innerPosX, innerPosY);
        this.cellRegisterEntry = cellRegisterEntry;
        this.chunkId = chunkId;
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
        if (obj instanceof CellComponent testCellComponent) {
            return innerPos == testCellComponent.innerPos && cellRegisterEntry == testCellComponent.cellRegisterEntry;
        }
        return false;
    }
}
