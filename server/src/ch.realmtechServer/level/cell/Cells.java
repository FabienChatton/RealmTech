package ch.realmtechServer.level.cell;

public class Cells {
    public enum Layer {
        GROUND((byte) 0),
        GROUND_DECO((byte) 1),
        BUILD((byte) 2),
        BUILD_DECO((byte) 3);
        public final byte layer;
        Layer(byte layer) {
            this.layer = layer;
        }
    }
    public static byte getInnerChunkPosY(byte innerChunkPos) {
        return (byte) (innerChunkPos & 0x0F);
    }

    public static byte getInnerChunkPosX(byte innerChunkPos) {
        return (byte) ((innerChunkPos >> 4) & 0x0F);
    }

    public static byte getInnerChunkPos(byte innerChunkPosX, byte innerChunkPosY) {
        return (byte) ((innerChunkPosX << 4) + innerChunkPosY);
    }
}