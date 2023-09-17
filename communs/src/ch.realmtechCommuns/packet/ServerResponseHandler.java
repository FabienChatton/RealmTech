package ch.realmtechCommuns.packet;

import ch.realmtechCommuns.packet.clientPacket.ClientExecute;
import io.netty.channel.Channel;

public interface ServerResponseHandler {
    void broadCastPacket(ClientPacket<ClientExecute> packet);

    void boardCastPacketExcept(ClientPacket<ClientExecute> packet, Channel... channel);

    void sendPacketTo(ClientPacket<ClientExecute> packet, Channel... channel);
}
