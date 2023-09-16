package ch.realmtechCommuns.packet;

import com.artemis.World;
import io.netty.channel.ChannelHandlerContext;

public interface ServerPacket extends Packet {
    void executeOnServer(final ChannelHandlerContext incomingCtx, final World world, final ServerResponseHandler serverResponseHandler);
}
