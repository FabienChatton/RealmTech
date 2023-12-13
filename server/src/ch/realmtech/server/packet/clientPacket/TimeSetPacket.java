package ch.realmtech.server.packet.clientPacket;

import ch.realmtech.server.packet.ClientPacket;
import io.netty.buffer.ByteBuf;

public class TimeSetPacket implements ClientPacket {
    private final float time;
    public TimeSetPacket(float time) {
        this.time = time;
    }

    public TimeSetPacket(ByteBuf byteBuf) {
        time = byteBuf.readFloat();
    }

    @Override
    public void write(ByteBuf byteBuf) {
        byteBuf.writeFloat(time);
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        clientExecute.timeSet(time);
    }

    @Override
    public int getSize() {
        return Float.BYTES;
    }
}
