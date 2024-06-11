package ch.realmtech.server.packet.clientPacket;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.packet.ClientPacket;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class DeconnectionJoueurPacket implements ClientPacket {
    private final UUID uuid;

    public DeconnectionJoueurPacket(UUID uuid) {
        this.uuid = uuid;
    }

    public DeconnectionJoueurPacket(ByteBuf byteBuf) {
        uuid = ByteBufferHelper.readUUID(byteBuf);
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        clientExecute.deconnectionJoueur(uuid);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        ByteBufferHelper.writeUUID(byteBuf, uuid);
    }

    @Override
    public int getSize() {
        return Long.SIZE * 2;
    }
}
