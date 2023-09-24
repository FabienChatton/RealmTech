package ch.realmtechServer.ecs.component;

import com.artemis.PooledComponent;
import com.artemis.annotations.EntityId;

import java.util.UUID;

public class InfChunkComponent extends PooledComponent {
    public int chunkPosX;
    public int chunkPosY;
    public UUID uuid;
    @EntityId
    public int[] infCellsId;

    public InfChunkComponent set(int chunkPosX, int chunkPosY, int[] infCellsId, UUID uuid) {
        this.chunkPosX = chunkPosX;
        this.chunkPosY = chunkPosY;
        this.infCellsId = infCellsId;
        this.uuid = uuid;
        return this;
    }

    @Override
    protected void reset() {
        chunkPosX = 0;
        chunkPosY = 0;
        uuid = null;
        infCellsId = null;
    }

    public int getTailleBytes() {
        // version protocole (int) + UUID 2x long, nombre de cells * taille cell (short) + chunkPosX (int) + chunkPosY (int)
        return Integer.BYTES + Long.BYTES * 2 + infCellsId.length * InfCellComponent.TAILLE_BYTES + Integer.BYTES + Integer.BYTES;
    }
}
