package ch.realmtechCommuns.packet;

import io.netty.buffer.ByteBuf;


public interface Packet {

    void write(ByteBuf byteBuf);
    default int getId() {
        return getClass().getSimpleName().hashCode();
    }
}
