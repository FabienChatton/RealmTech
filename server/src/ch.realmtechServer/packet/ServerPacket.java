package ch.realmtechServer.packet;

import ch.realmtechServer.packet.serverPacket.ServerExecute;
import io.netty.channel.Channel;

public interface ServerPacket extends Packet {
    void executeOnServer(final Channel clientChannel, ServerExecute serverExecute);
}
