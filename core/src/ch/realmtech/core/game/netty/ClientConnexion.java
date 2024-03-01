package ch.realmtech.core.game.netty;

import ch.realmtech.server.packet.ServerPacket;

import java.io.Closeable;

public interface ClientConnexion extends Closeable {
    void sendAndFlushPacketToServer(ServerPacket serverPacket);
}
