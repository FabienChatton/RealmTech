package ch.realmtech.server.packet.serverPacket;

import ch.realmtech.server.packet.ServerPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public class TimeGetRequestPacket implements ServerPacket {

    public TimeGetRequestPacket(ByteBuf byteBuf) {
    }

    public TimeGetRequestPacket() {
    }

    @Override
    public void write(ByteBuf byteBuf) {

    }

    @Override
    public void executeOnServer(Channel clientChannel, ServerExecute serverExecute) {
        serverExecute.getTime(clientChannel);
    }

    @Override
    public int getSize() {
        return 0;
    }
}
