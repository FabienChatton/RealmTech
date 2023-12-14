package ch.realmtech.server.packet.clientPacket;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.packet.ClientPacket;
import ch.realmtech.server.serialize.SerializerController;
import ch.realmtech.server.serialize.types.SerializedApplicationBytes;
import io.netty.buffer.ByteBuf;

public class PhysicEntitySetPacket implements ClientPacket {
    private final SerializedApplicationBytes physicEntityBytes;

    public PhysicEntitySetPacket(SerializedApplicationBytes physicEntityBytes) {
        this.physicEntityBytes = physicEntityBytes;
    }

    public PhysicEntitySetPacket(ByteBuf byteBuf) {
        physicEntityBytes = ByteBufferHelper.readSerializedApplicationBytes(byteBuf);
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        clientExecute.getContext().getWorld().getRegistered(SerializerController.class).getPhysicEntitySerializerController().decode(physicEntityBytes).accept(clientExecute);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        ByteBufferHelper.writeSerializedApplicationBytes(byteBuf, physicEntityBytes);
    }

    @Override
    public int getSize() {
        return physicEntityBytes.getLength();
    }
}
