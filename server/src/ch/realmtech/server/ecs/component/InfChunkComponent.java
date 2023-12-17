package ch.realmtech.server.ecs.component;

import com.artemis.ComponentMapper;
import com.artemis.PooledComponent;
import com.artemis.annotations.EntityId;

public class InfChunkComponent extends PooledComponent {
    public int chunkPosX;
    public int chunkPosY;
    @EntityId
    public int[] infCellsId;

    public InfChunkComponent set(int chunkPosX, int chunkPosY, int[] infCellsId) {
        this.chunkPosX = chunkPosX;
        this.chunkPosY = chunkPosY;
        this.infCellsId = infCellsId;
        return this;
    }

    @Override
    protected void reset() {
        chunkPosX = 0;
        chunkPosY = 0;
        infCellsId = null;
    }

    @Override
    public String toString() {
        return String.format("x: %d, y: %d, size: %d", chunkPosX, chunkPosY, infCellsId.length);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof InfChunkComponent testInfChunkComponent) {
            if (chunkPosX == testInfChunkComponent.chunkPosX && chunkPosY == testInfChunkComponent.chunkPosY) {
                return infCellsId.length == testInfChunkComponent.infCellsId.length;
            }
        }
        return false;
    }

    public boolean deepEquals(Object obj, ComponentMapper<CellComponent> mCell) {
        if (equals(obj)) {
            InfChunkComponent testInfChunkComponent = (InfChunkComponent) obj;
            for (int i = 0; i < infCellsId.length; i++) {
                if (!mCell.get(infCellsId[i]).equals(mCell.get(testInfChunkComponent.infCellsId[i]))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
