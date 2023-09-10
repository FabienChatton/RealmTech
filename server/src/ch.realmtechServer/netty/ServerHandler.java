package ch.realmtechServer.netty;

import ch.realmtechServer.netty.packet.Packet;
import ch.realmtechServer.netty.packet.PlayerConnectionPacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ServerHandler extends SimpleChannelInboundHandler<Packet> {

    private static final ChannelGroup channels;

    static {
        channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel incomingChannel = ctx.channel();
        System.out.println("[SERVER] - " + incomingChannel.remoteAddress() + " c'est connecté au serveur");
        channels.forEach(channel -> channel.writeAndFlush(new PlayerConnectionPacket(0, 0)));
        channels.add(incomingChannel);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel incomingChannel = ctx.channel();
        System.out.println("[SERVER] - " + incomingChannel.remoteAddress() + " c'est déconnecté du serveur");
        channels.remove(incomingChannel);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet msg) throws Exception {
        System.out.println(msg);
    }
}
