package ch.realmtech.game.netty;

import ch.realmtechCommuns.packet.Packet;
import com.artemis.World;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientHandler extends SimpleChannelInboundHandler<Packet> {
    private final World world;

    public ClientHandler(World world) {
        this.world = world;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet msg) throws Exception {
        msg.executeOnClient(world);
    }
}
