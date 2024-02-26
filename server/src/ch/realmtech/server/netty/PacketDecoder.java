package ch.realmtech.server.netty;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.packet.Packet;
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
        if (in.readableBytes() < Integer.BYTES) {
            throw new Exception("Packet received but to small to read packetId");
        }
        int packetId = in.readInt();
        if (!ServerContext.PACKETS.containsKey(packetId)) {
            throw new Exception("Packet id not identified. Packet id:" + packetId);
        } else {
            Packet incomingPacket = ServerContext.PACKETS.get(packetId).apply(in);
            out.add(incomingPacket);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Exception Caught: {}", cause.getMessage());
        ctx.channel().close();
    }
}
