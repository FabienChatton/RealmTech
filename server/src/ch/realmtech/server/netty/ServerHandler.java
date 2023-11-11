package ch.realmtech.server.netty;

import ch.realmtech.server.packet.ServerPacket;
import ch.realmtech.server.packet.serverPacket.ServerExecute;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerHandler extends SimpleChannelInboundHandler<ServerPacket> {
    private final static Logger logger = LoggerFactory.getLogger(ServerHandler.class);
    public static final ChannelGroup channels;
    private final ServerExecute serverExecute;

    static {
        channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    }

    public ServerHandler(ServerExecute serverExecute) {
        this.serverExecute = serverExecute;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel incomingChannel = ctx.channel();
        logger.info("{} c'est connecté du serveur", incomingChannel.remoteAddress());
        channels.add(incomingChannel);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel incomingChannel = ctx.channel();
        logger.info("{} c'est déconnecté du serveur", incomingChannel.remoteAddress());
        serverExecute.removePlayer(incomingChannel);
        channels.remove(incomingChannel);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ServerPacket msg) throws Exception {
        msg.executeOnServer(ctx.channel(), serverExecute);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        logger.error("Une erreur est survenue {}", cause.getMessage());
        ctx.channel().close();
    }
}
