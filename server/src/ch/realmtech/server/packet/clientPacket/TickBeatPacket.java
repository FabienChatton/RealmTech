package ch.realmtech.server.packet.clientPacket;

import ch.realmtech.server.packet.ClientPacket;
import io.netty.buffer.ByteBuf;

public class TickBeatPacket implements ClientPacket {
    private final float tickElapseTime;

    public TickBeatPacket(float tickElapseTime) {
        this.tickElapseTime = tickElapseTime;
    }

    public TickBeatPacket(ByteBuf byteBuf) {
        tickElapseTime = byteBuf.readFloat();
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        clientExecute.tickBeat(tickElapseTime);
    }

    @Override
    public int getSize() {
        return Float.SIZE;
    }

    @Override
    public void write(ByteBuf byteBuf) {
        byteBuf.writeFloat(tickElapseTime);
    }
}
