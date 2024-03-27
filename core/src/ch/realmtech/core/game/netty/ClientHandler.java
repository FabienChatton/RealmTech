package ch.realmtech.core.game.netty;

import ch.realmtech.server.packet.ClientPacket;
import ch.realmtech.server.packet.clientPacket.ClientExecute;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientHandler extends SimpleChannelInboundHandler<ClientPacket> {
    private final ClientExecute clientExecute;

    public ClientHandler(ClientExecute clientExecute) {
        this.clientExecute = clientExecute;
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        clientExecute.clientConnexionRemoved();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ClientPacket msg) throws Exception {
        msg.executeOnClient(clientExecute);
    }
}
