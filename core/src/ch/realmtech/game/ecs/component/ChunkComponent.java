package ch.realmtech.game.ecs.component;

import com.artemis.PooledComponent;
import com.artemis.annotations.EntityId;

public class ChunkComponent extends PooledComponent {
    @EntityId
    public int saveId = -1;
    public int chunkPossX;
    public int chunkPossY;

    public void set(int saveId, int chunkPossX, int chunkPossY) {
        this.saveId = saveId;
        this.chunkPossX = chunkPossX;
        this.chunkPossY = chunkPossY;
    }

    @Override
    protected void reset() {
        saveId = -1;
        chunkPossX = 0;
        chunkPossY = 0;
    }
}
