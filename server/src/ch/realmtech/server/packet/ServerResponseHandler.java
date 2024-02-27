package ch.realmtech.server.packet;

import io.netty.channel.Channel;

public interface ServerResponseHandler {
    void broadCastPacket(ClientPacket packet);

    void sendPacketToSubscriberForEntityId(ClientPacket packet, int entityIdSubscription);

    void sendPacketToSubscriberForChunkPos(ClientPacket packet, int chunkPosX, int chunkPosY);

    void broadCastPacketExcept(ClientPacket packet, Channel... channel);

    void sendPacketTo(ClientPacket packet, Channel... channel);

    void sendPacketTo(ClientPacket packet, Channel channel);
}
