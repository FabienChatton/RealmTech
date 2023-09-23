package ch.realmtechServer.ecs.component;

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

    public int getTailleBytes() {
        // version protocole (int) + nombre de cells * taille cell (short) + chunkPosX (int) + chunkPosY (int)
        return Integer.BYTES + infCellsId.length * InfCellComponent.TAILLE_BYTES + Integer.BYTES + Integer.BYTES;
    }
}
