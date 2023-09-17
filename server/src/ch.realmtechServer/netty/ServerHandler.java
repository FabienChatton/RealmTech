package ch.realmtechServer.netty;

import ch.realmtechCommuns.packet.ClientPacket;
import ch.realmtechCommuns.packet.ServerPacket;
import ch.realmtechCommuns.packet.ServerResponseHandler;
import ch.realmtechCommuns.packet.clientPacket.ClientExecute;
import ch.realmtechCommuns.packet.serverPacket.ServerExecute;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class ServerHandler extends SimpleChannelInboundHandler<ServerPacket> implements ServerResponseHandler {
    private final static Logger logger = LoggerFactory.getLogger(ServerHandler.class);
    private static final ChannelGroup channels;
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
        channels.remove(incomingChannel);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ServerPacket msg) throws Exception {
        msg.executeOnServer(ctx.channel(), this, serverExecute);
    }

    @Override
    public void broadCastPacket(ClientPacket<ClientExecute> packet) {
        logger.trace("envoie du packet {} en broadCast", packet.getClass().getSimpleName());
        channels.forEach(channel -> channel.writeAndFlush(packet));
    }

    @Override
    public void boardCastPacketExcept(ClientPacket<ClientExecute> packet, Channel... channels) {
        List<Channel> channelFiltre = ServerHandler.channels.stream()
                .filter(channel -> Arrays.stream(channels).anyMatch(c -> c != channel))
                .toList();
        logger.trace("envoie du packet {} vers {}", packet.getClass().getSimpleName(), channelFiltre);
        channelFiltre
                .forEach(channel -> channel.writeAndFlush(packet));
    }

    @Override
    public void sendPacketTo(ClientPacket<ClientExecute> packet, Channel... channels) {
        logger.trace("envoie du packet {} vers {}", packet.getClass().getSimpleName(), Arrays.toString(channels));
        Arrays.stream(channels)
                .forEach(channel -> channel.writeAndFlush(packet));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        logger.error("Une erreur est survenue {}", cause.getMessage());
    }
}
