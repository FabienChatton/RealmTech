package ch.realmtech.server.packet.clientPacket;

import ch.realmtech.server.packet.ClientPacket;
import io.netty.buffer.ByteBuf;

public class TickBeatPacket implements ClientPacket {
    private final float tickElapseTime;
    private final float deltaTime;

    public TickBeatPacket(float tickElapseTime, float deltaTime) {
        this.tickElapseTime = tickElapseTime;
        this.deltaTime = deltaTime;
    }

    public TickBeatPacket(ByteBuf byteBuf) {
        tickElapseTime = byteBuf.readFloat();
        deltaTime = byteBuf.readFloat();
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        clientExecute.tickBeat(tickElapseTime, deltaTime);
    }

    @Override
    public int getSize() {
        return Float.SIZE * 2;
    }

    @Override
    public void write(ByteBuf byteBuf) {
        byteBuf.writeFloat(tickElapseTime);
        byteBuf.writeFloat(deltaTime);
    }
}
