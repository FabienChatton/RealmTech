package ch.realmtechCommuns.packet.serverPacket;

import ch.realmtechCommuns.packet.ServerPacket;
import ch.realmtechCommuns.packet.ServerResponseHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public class DemandeDeConnectionJoueurPacket implements ServerPacket {

    public DemandeDeConnectionJoueurPacket() {
    }

    public DemandeDeConnectionJoueurPacket(ByteBuf byteBuf) {

    }

    @Override
    public void write(ByteBuf byteBuf) {

    }

    @Override
    public void executeOnServer(Channel clientChannel, ServerResponseHandler serverResponseHandler, ServerExecute serverExecute) {
        serverExecute.newPlayerConnect(clientChannel, serverResponseHandler);
    }
}
