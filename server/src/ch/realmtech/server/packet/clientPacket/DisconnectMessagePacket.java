package ch.realmtech.server.packet.clientPacket;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.packet.ClientPacket;
import io.netty.buffer.ByteBuf;

public class DisconnectMessagePacket implements ClientPacket {
    private final String message;

    public DisconnectMessagePacket(String message) {
        this.message = message;
    }

    public DisconnectMessagePacket(ByteBuf byteBuf) {
        message = ByteBufferHelper.getString(byteBuf);
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        clientExecute.disconnectMessage(message);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        ByteBufferHelper.writeString(byteBuf, message);
    }

    @Override
    public int getSize() {
        return message.length() + 1;
    }
}
