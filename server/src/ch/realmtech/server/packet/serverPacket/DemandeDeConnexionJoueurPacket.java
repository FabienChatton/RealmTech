package ch.realmtech.server.packet.serverPacket;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.packet.ServerPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public class DemandeDeConnexionJoueurPacket implements ServerPacket {
    private final String username;
    public DemandeDeConnexionJoueurPacket(String username) {
        this.username = username;
    }

    public DemandeDeConnexionJoueurPacket(ByteBuf byteBuf) {
        username = ByteBufferHelper.getString(byteBuf);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        ByteBufferHelper.writeString(byteBuf, username);
    }

    @Override
    public void executeOnServer(Channel clientChannel, ServerExecute serverExecute) {
        serverExecute.connexionPlayerRequest(clientChannel, username);
    }

    @Override
    public int getSize() {
        return username.length() + 1;
    }
}
