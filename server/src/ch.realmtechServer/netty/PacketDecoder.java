package ch.realmtechServer.netty;

import ch.realmtechCommuns.packet.Packet;
import ch.realmtechServer.ServerContext;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {
    private final static Logger logger = LoggerFactory.getLogger(PacketDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int packetId = in.readInt();
        Packet incomingPacket = ServerContext.PACKETS.get(packetId).apply(in);
        out.add(incomingPacket);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        logger.error("Une erreur est survenue {}", cause.getMessage());
    }
}
