package ch.realmtech.server.packet;

import ch.realmtech.server.packet.serverPacket.ServerExecute;
import io.netty.channel.Channel;

public interface ServerPacket extends Packet {
    void executeOnServer(final Channel clientChannel, ServerExecute serverExecute);
}
