package ch.realmtech.server.packet.serverPacket;

import ch.realmtech.server.packet.ServerPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public class AskPlayerRespawn implements ServerPacket {

    public AskPlayerRespawn() {
    }

    public AskPlayerRespawn(ByteBuf byteBuf) {
    }

    @Override
    public void write(ByteBuf byteBuf) {

    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public void executeOnServer(Channel clientChannel, ServerExecute serverExecute) {
        serverExecute.askPlayerRespawn(clientChannel);
    }
}
