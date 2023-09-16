package ch.realmtech.game.netty;

import ch.realmtechCommuns.packet.ClientPacket;
import ch.realmtechCommuns.packet.clientPacket.ClientExecute;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientHandler extends SimpleChannelInboundHandler<ClientPacket<ClientExecute>> {
    private final ClientExecute clientExecute;

    public ClientHandler(ClientExecute clientExecute) {
        this.clientExecute = clientExecute;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ClientPacket<ClientExecute> msg) throws Exception {
        msg.executeOnClient(clientExecute);
    }
}
