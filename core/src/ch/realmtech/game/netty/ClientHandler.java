package ch.realmtech.game.netty;

import ch.realmtech.RealmTech;
import ch.realmtechServer.netty.packet.Packet;
import ch.realmtechServer.netty.packet.PlayerConnectionPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientHandler extends SimpleChannelInboundHandler<Packet> {
    private final RealmTech context;
    public ClientHandler(RealmTech context) {
        this.context = context;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet msg) throws Exception {
        if (msg instanceof PlayerConnectionPacket playerConnectionPacket) {
            context.getEcsEngine().createPlayer(playerConnectionPacket.getX(), playerConnectionPacket.getY());
        }
    }
}
