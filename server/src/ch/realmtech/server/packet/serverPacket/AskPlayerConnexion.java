package ch.realmtech.server.packet.serverPacket;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.packet.ServerPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public class AskPlayerConnexion implements ServerPacket {
    private final String username;

    public AskPlayerConnexion(String username) {
        this.username = username;
    }

    public AskPlayerConnexion(ByteBuf byteBuf) {
        username = ByteBufferHelper.getString(byteBuf);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        ByteBufferHelper.writeString(byteBuf, username);
    }

    @Override
    public void executeOnServer(Channel clientChannel, ServerExecute serverExecute) {
        serverExecute.askPlayerConnexion(clientChannel, username);
    }

    @Override
    public int getSize() {
        return username.length() + 1;
    }
}
