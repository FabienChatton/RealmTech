package ch.realmtechServer.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    private final List<Channel> channels = new ArrayList<>();

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel incomingChannel = ctx.channel();
        System.out.println("[SERVER] - " + incomingChannel.remoteAddress() + " c'est connecté au serveur");
        channels.add(incomingChannel);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel incomingChannel = ctx.channel();
        System.out.println("[SERVER] - " + incomingChannel.remoteAddress() + " c'est déconnecté du serveur");
        channels.remove(incomingChannel);
    }
}
