package ch.realmtech.game.netty;

import ch.realmtechCommuns.packet.ClientPacket;
import com.artemis.World;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientHandler extends SimpleChannelInboundHandler<ClientPacket> {
    private final World world;

    public ClientHandler(World world) {
        this.world = world;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ClientPacket msg) throws Exception {
        msg.executeOnClient(world);
    }
}
