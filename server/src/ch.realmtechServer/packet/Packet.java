package ch.realmtechServer.packet;

import io.netty.buffer.ByteBuf;


public interface Packet {

    void write(ByteBuf byteBuf);
    default int getId() {
        return getClass().getSimpleName().hashCode();
    }

    /** Donnes la taille du packet en bits */
    int getSize();
}
