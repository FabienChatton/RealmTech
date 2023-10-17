package ch.realmtechServer.packet.serverPacket;

import ch.realmtechServer.packet.ServerPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public class GetPlayerInventorySessionPacket implements ServerPacket {
    public GetPlayerInventorySessionPacket() {
    }

    public GetPlayerInventorySessionPacket(ByteBuf byteBuf) {
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
        serverExecute.getPlayerInventorySession(clientChannel);
    }
}
