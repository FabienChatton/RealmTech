package ch.realmtech.game.ecs.component;

import com.artemis.PooledComponent;
import com.artemis.annotations.EntityId;

public class InfChunkComponent extends PooledComponent {
    public int chunkPossX;
    public int chunkPossY;
    @EntityId
    public int[] infLayers;

    public InfChunkComponent set(int chunkPossX, int chunkPossY, int[] infLayers) {
        this.chunkPossX = chunkPossX;
        this.chunkPossY = chunkPossY;
        this.infLayers = infLayers;
        return this;
    }

    @Override
    protected void reset() {
        chunkPossX = 0;
        chunkPossY = 0;
        infLayers = null;
    }
}
