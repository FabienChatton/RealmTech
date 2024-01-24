package ch.realmtech.server.packet.serverPacket;

import ch.realmtech.server.packet.ServerPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public class RotateFaceCellRequestPacket implements ServerPacket {
    private final int worldPosX;
    private final int worldPosY;
    private final byte layer;
    private final byte faceToRotate;

    public RotateFaceCellRequestPacket(int worldPosX, int worldPosY, byte layer, byte faceToRotate) {
        this.worldPosX = worldPosX;
        this.worldPosY = worldPosY;
        this.layer = layer;
        this.faceToRotate = faceToRotate;
    }

    public RotateFaceCellRequestPacket(ByteBuf byteBuf) {
        worldPosX = byteBuf.readInt();
        worldPosY = byteBuf.readInt();
        layer = byteBuf.readByte();
        faceToRotate = byteBuf.readByte();
    }

    @Override
    public void write(ByteBuf byteBuf) {
        byteBuf.writeInt(worldPosX);
        byteBuf.writeInt(worldPosY);
        byteBuf.writeByte(layer);
        byteBuf.writeByte(faceToRotate);
    }

    @Override
    public int getSize() {
        return Integer.BYTES * 2 + Byte.BYTES * 2;
    }

    @Override
    public void executeOnServer(Channel clientChannel, ServerExecute serverExecute) {
        serverExecute.rotateFaceCellRequest(clientChannel, worldPosX, worldPosY, layer, faceToRotate);
    }
}
