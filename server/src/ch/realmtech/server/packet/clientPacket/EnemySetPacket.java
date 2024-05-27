package ch.realmtech.server.packet.clientPacket;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.packet.ClientPacket;
import ch.realmtech.server.serialize.SerializerController;
import ch.realmtech.server.serialize.physicEntity.PhysicEntityArgs;
import ch.realmtech.server.serialize.types.SerializedApplicationBytes;
import io.netty.buffer.ByteBuf;

public class EnemySetPacket implements ClientPacket {
    private final SerializedApplicationBytes physicEntityBytes;

    public EnemySetPacket(SerializedApplicationBytes physicEntityBytes) {
        this.physicEntityBytes = physicEntityBytes;
    }

    public EnemySetPacket(ByteBuf byteBuf) {
        physicEntityBytes = ByteBufferHelper.readSerializedApplicationBytes(byteBuf);
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        PhysicEntityArgs physicEntityArgs = clientExecute.getContext().getWorld().getRegistered(SerializerController.class).getEnemySerializerController().decode(physicEntityBytes);
        clientExecute.physicEntity(physicEntityArgs);
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
