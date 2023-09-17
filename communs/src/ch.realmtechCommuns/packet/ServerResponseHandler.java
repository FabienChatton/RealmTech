package ch.realmtechCommuns.packet;

import io.netty.channel.Channel;

public interface ServerResponseHandler {
    void broadCastPacket(ClientPacket packet);

    void boardCastPacketExcept(ClientPacket packet, Channel... channel);

    void sendPacketTo(ClientPacket packet, Channel... channel);
}
