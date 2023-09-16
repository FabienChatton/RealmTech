package ch.realmtechCommuns.packet.clientPacket;

import ch.realmtechCommuns.packet.ClientPacket;
import io.netty.buffer.ByteBuf;

public class ConnectionJoueurReussitPacket implements ClientPacket<ClientExecute> {

    public ConnectionJoueurReussitPacket() {
    }

    public ConnectionJoueurReussitPacket(ByteBuf byteBuf) {

    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        clientExecute.setScreenToGameScreen();
    }

    @Override
    public void write(ByteBuf byteBuf) {

    }

}
