package ch.realmtech.server.packet;

import io.netty.channel.Channel;

public interface ServerResponseHandler {
    void broadCastPacket(ClientPacket packet);

    void broadCastPacketInRange(ClientPacket packet, int playersSrc);

    void broadCastPacketExcept(ClientPacket packet, Channel... channel);

    void sendPacketTo(ClientPacket packet, Channel... channel);

    void sendPacketTo(ClientPacket packet, Channel channel);
}
