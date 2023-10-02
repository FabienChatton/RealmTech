package ch.realmtech.game.netty;

import ch.realmtechServer.packet.ClientPacket;
import ch.realmtechServer.packet.clientPacket.ClientExecute;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientHandler extends SimpleChannelInboundHandler<ClientPacket> {
    private final ClientExecute clientExecute;

    public ClientHandler(ClientExecute clientExecute) {
        this.clientExecute = clientExecute;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ClientPacket msg) throws Exception {
        msg.executeOnClient(clientExecute);
    }
}
