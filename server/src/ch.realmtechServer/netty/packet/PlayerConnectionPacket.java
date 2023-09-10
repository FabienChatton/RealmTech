package ch.realmtechServer.netty.packet;

import io.netty.buffer.ByteBuf;

public class PlayerConnectionPacket implements Packet {
    private final float x;
    private final float y;

    public PlayerConnectionPacket(ByteBuf byteBuf) {
        x = byteBuf.readFloat();
        y = byteBuf.readFloat();
    }

    public PlayerConnectionPacket(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void write(ByteBuf byteBuf) {
        byteBuf.writeFloat(x);
        byteBuf.writeFloat(y);
    }
}
