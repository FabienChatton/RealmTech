package ch.realmtechServer.netty;

import ch.realmtechCommuns.packet.Packet;
import ch.realmtechServer.ServerContext;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int packetId = in.readInt();
        Packet incomingPacket = ServerContext.packets.get(packetId).apply(in);
        out.add(incomingPacket);
    }
}
