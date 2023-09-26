package ch.realmtechServer.ecs.component;

import ch.realmtechServer.level.cell.Cells;
import ch.realmtechServer.registery.CellRegisterEntry;
import com.artemis.PooledComponent;
import com.artemis.World;

import java.nio.ByteBuffer;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;

public class InfCellComponent extends PooledComponent {
    public final static Byte TAILLE_BYTES = 6;
    private byte innerPos;
    public CellRegisterEntry cellRegisterEntry;

    public InfCellComponent set(byte innerPosX, byte innerPosY, CellRegisterEntry cellRegisterEntry) {
        innerPos = Cells.getInnerChunkPos(innerPosX, innerPosY);
        this.cellRegisterEntry = cellRegisterEntry;
        return this;
    }

    public byte[] toBytes() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(InfCellComponent.TAILLE_BYTES);
        byteBuffer.putInt(CellRegisterEntry.getHash(cellRegisterEntry));
        byteBuffer.put(Cells.getInnerChunkPos(getInnerPosX(), getInnerPosY()));
        return byteBuffer.array();
    }

    public static FromBytesArgs fromBytes(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        int hashRegistry = byteBuffer.getInt();
        byte pos = byteBuffer.get();
        byte posX = Cells.getInnerChunkPosX(pos);
        byte posY = Cells.getInnerChunkPosY(pos);
        final CellRegisterEntry cellRegisterEntry = CellRegisterEntry.getCellModAndCellHash(hashRegistry);
        return new FromBytesArgs(posX, posY, cellRegisterEntry);
    }

    public record FromBytesArgs(byte posX, byte posY, CellRegisterEntry cellRegisterEntry){ }

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
