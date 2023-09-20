package ch.realmtechCommuns.packet;

import ch.realmtechCommuns.packet.serverPacket.ServerExecute;
import io.netty.channel.Channel;

public interface ServerPacket extends Packet {
    void executeOnServer(final Channel clientChannel, ServerExecute serverExecute);
}
