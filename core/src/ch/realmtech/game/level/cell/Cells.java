package ch.realmtech.game.level.cell;

public class Cells {
    public static byte getInnerChunkPossY(byte innerChunkPoss) {
        return (byte) (innerChunkPoss & 0x0F);
    }

    public static byte getInnerChunkPossX(byte innerChunkPoss) {
        return (byte) ((innerChunkPoss >> 4) & 0x0F);
    }

    public static byte getInnerChunkPoss(byte innerChunkPossX, byte innerChunkPossY) {
        return (byte) ((innerChunkPossX << 4) + innerChunkPossY);
    }
}