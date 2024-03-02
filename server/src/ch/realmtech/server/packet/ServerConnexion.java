package ch.realmtech.server.packet;

import io.netty.channel.Channel;

import java.util.UUID;

public interface ServerConnexion {
    void broadCastPacket(ClientPacket packet);

    void sendPacketToSubscriberForEntity(ClientPacket packet, UUID entitySubscription);

    void sendPacketToSubscriberForChunkPos(ClientPacket packet, int chunkPosX, int chunkPosY);

    void broadCastPacketExcept(ClientPacket packet, Channel... channels);

    void sendPacketTo(ClientPacket packet, Channel channel);
}
