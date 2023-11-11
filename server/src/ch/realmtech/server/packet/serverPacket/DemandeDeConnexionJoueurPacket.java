package ch.realmtech.server.packet.serverPacket;

import ch.realmtech.server.packet.ServerPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public class DemandeDeConnexionJoueurPacket implements ServerPacket {

    public DemandeDeConnexionJoueurPacket() {
    }

    public DemandeDeConnexionJoueurPacket(ByteBuf byteBuf) {

    }

    @Override
    public void write(ByteBuf byteBuf) {

    }

    @Override
    public void executeOnServer(Channel clientChannel, ServerExecute serverExecute) {
        serverExecute.newPlayerConnect(clientChannel);
    }

    @Override
    public int getSize() {
        return 0;
    }
}