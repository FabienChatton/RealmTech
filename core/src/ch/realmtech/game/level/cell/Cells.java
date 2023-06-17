package ch.realmtech.game.level.cell;

public class Cells {
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