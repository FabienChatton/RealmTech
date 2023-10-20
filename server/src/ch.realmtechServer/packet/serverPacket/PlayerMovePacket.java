package ch.realmtechServer.packet.serverPacket;

import ch.realmtechServer.packet.ServerPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public class PlayerMovePacket implements ServerPacket {
    private final byte inputKeys;

    public PlayerMovePacket(byte inputKeys) {
        this.inputKeys = inputKeys;
    }

    public PlayerMovePacket(ByteBuf byteBuf) {
        this.inputKeys = byteBuf.readByte();
    }

    @Override
    public void write(ByteBuf byteBuf) {
        byteBuf.writeByte(inputKeys);
    }

    @Override
    public int getSize() {
        return Byte.SIZE;
    }

    @Override
    public void executeOnServer(Channel clientChannel, ServerExecute serverExecute) {
        serverExecute.playerMove(clientChannel, inputKeys);
    }
}
