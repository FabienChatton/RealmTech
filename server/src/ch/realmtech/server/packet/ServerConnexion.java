package ch.realmtech.server.packet;

import io.netty.channel.Channel;

public interface ServerConnexion {
    void broadCastPacket(ClientPacket packet);

    void sendPacketToSubscriberForEntityId(ClientPacket packet, int entityIdSubscription);

    void sendPacketToSubscriberForChunkPos(ClientPacket packet, int chunkPosX, int chunkPosY);

    void broadCastPacketExcept(ClientPacket packet, Channel... channels);

    void sendPacketTo(ClientPacket packet, Channel channel);
}
