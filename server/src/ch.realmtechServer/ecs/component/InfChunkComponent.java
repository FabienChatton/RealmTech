package ch.realmtechServer.ecs.component;

import ch.realmtechServer.ecs.system.MapManager;
import com.artemis.ComponentMapper;
import com.artemis.PooledComponent;
import com.artemis.World;
import com.artemis.annotations.EntityId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

import static ch.realmtechServer.ecs.system.SaveInfManager.SAVE_PROTOCOLE_VERSION;

public class InfChunkComponent extends PooledComponent {
    private final static Logger logger = LoggerFactory.getLogger(InfChunkComponent.class);
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

    public byte[] toBytes(ComponentMapper<InfCellComponent> mCell) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(getTailleBytes());
        // Métadonnées
        byteBuffer.putInt(SAVE_PROTOCOLE_VERSION);
        // header
        byteBuffer.putShort((short) infCellsId.length);
        // body
        for (int i = 0; i < infCellsId.length; i++) {
            byteBuffer.put(mCell.get(infCellsId[i]).toBytes());
        }
        return byteBuffer.array();
    }

    public static int fromByte(World world, byte[] bytes, int chunkPosX, int chunkPosY) {
        ByteBuffer inputWrap = ByteBuffer.wrap(bytes);
        int versionProtocole = inputWrap.getInt();
        if (versionProtocole != SAVE_PROTOCOLE_VERSION) {
            logger.error("La version de la sauvegarde ne correspond pas. Fichier : {}. Jeu : {}", versionProtocole, SAVE_PROTOCOLE_VERSION);
        }
        // Header
        short nombreDeCellule = inputWrap.getShort();
        // Body
        int[] cellulesId = new int[nombreDeCellule];
        int chunkId = world.create();
        world.edit(chunkId).create(InfChunkComponent.class).set(chunkPosX, chunkPosY, cellulesId);
        for (int i = 0; i < cellulesId.length; i++) {
            byte[] cellBuff = new byte[InfCellComponent.TAILLE_BYTES];
            inputWrap.get(cellBuff);
            InfCellComponent.FromBytesArgs cellFromArgs = InfCellComponent.fromBytes(cellBuff);
            cellulesId[i] = world.getSystem(MapManager.class).newCell(chunkId, chunkPosX, chunkPosY, cellFromArgs.innerPosX(), cellFromArgs.innerPosY(), cellFromArgs.cellRegisterEntry());
        }
        return chunkId;
    }

    @Override
    protected void reset() {
        chunkPosX = 0;
        chunkPosY = 0;
        infCellsId = null;
    }

    public int getTailleBytes() {
        // version protocole (int) + nombre de cells (short) * taille cell (bytes) + chunkPosX (int) + chunkPosY (int)
        return Integer.BYTES + infCellsId.length * InfCellComponent.TAILLE_BYTES + Integer.BYTES + Integer.BYTES;
    }

    @Override
    public String toString() {
        return String.format("x: %d, y: %d, size: %d", chunkPosX, chunkPosY, infCellsId.length);
    }
}
