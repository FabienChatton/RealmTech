package ch.realmtech.game.ecs.component;

import com.artemis.PooledComponent;
import com.artemis.annotations.EntityId;

public class InfChunkComponent extends PooledComponent {
    public int chunkPossX;
    public int chunkPossY;
    @EntityId
    public int[] infCellsId;

    public InfChunkComponent set(int chunkPossX, int chunkPossY, int[] infCellsId) {
        this.chunkPossX = chunkPossX;
        this.chunkPossY = chunkPossY;
        this.infCellsId = infCellsId;
        return this;
    }

    @Override
    protected void reset() {
        chunkPossX = 0;
        chunkPossY = 0;
        infCellsId = null;
    }
}
