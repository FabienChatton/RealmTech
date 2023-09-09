package ch.realmtech.game.netty;

import ch.realmtechServer.netty.RealmtechPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientHandler extends SimpleChannelInboundHandler<RealmtechPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RealmtechPacket msg) throws Exception {
        System.out.println(msg);
    }
}
