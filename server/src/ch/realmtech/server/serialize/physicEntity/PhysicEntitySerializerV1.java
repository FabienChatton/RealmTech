package ch.realmtech.server.serialize.physicEntity;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.ecs.component.ItemComponent;
import ch.realmtech.server.ecs.component.PlayerConnexionComponent;
import ch.realmtech.server.ecs.component.PositionComponent;
import ch.realmtech.server.ecs.component.UuidComponent;
import ch.realmtech.server.serialize.Serializer;
import ch.realmtech.server.serialize.SerializerController;
import ch.realmtech.server.serialize.types.SerializedRawBytes;
import com.artemis.ComponentMapper;
import com.artemis.World;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.UUID;

import static ch.realmtech.server.serialize.physicEntity.PhysicEntitySerializerController.*;

public class PhysicEntitySerializerV1 implements Serializer<Integer, PhysicEntityArgs> {
    @Override
    public SerializedRawBytes toRawBytes(World world, SerializerController serializerController, Integer physicEntityToSerialize) {
        ByteBuf buffer = Unpooled.buffer(getBytesSize(world, serializerController, physicEntityToSerialize));

        ComponentMapper<PositionComponent> mPos = world.getMapper(PositionComponent.class);
        ComponentMapper<UuidComponent> mUuid = world.getMapper(UuidComponent.class);
        ComponentMapper<PlayerConnexionComponent> mPlayerConnexion = world.getMapper(PlayerConnexionComponent.class);
        ComponentMapper<ItemComponent> mItem = world.getMapper(ItemComponent.class);

        PositionComponent positionComponent = mPos.get(physicEntityToSerialize);
        UUID uuid = mUuid.get(physicEntityToSerialize).getUuid();
        byte flag;
        if (mPlayerConnexion.has(physicEntityToSerialize)) {
            flag = PLAYER_FLAG;
        } else if (mItem.has(physicEntityToSerialize)) {
            flag = ITEM_FLAG;
        } else {
           flag = ENEMY_FLAG;
        }

        ByteBufferHelper.writeUUID(buffer, uuid);
        buffer.writeFloat(positionComponent.x);
        buffer.writeFloat(positionComponent.y);
        buffer.writeByte(flag);
        return new SerializedRawBytes(buffer.array());
    }

    @Override
    public PhysicEntityArgs fromBytes(World world, SerializerController serializerController, SerializedRawBytes rawBytes) {
        ByteBuf buffer = Unpooled.wrappedBuffer(rawBytes.rawBytes());

        UUID uuid = ByteBufferHelper.readUUID(buffer);
        float x = buffer.readFloat();
        float y = buffer.readFloat();
        byte flag = buffer.readByte();
        return new PhysicEntityArgs(uuid, x, y, flag);
    }

    @Override
    public int getBytesSize(World world, SerializerController serializerController, Integer physicEntityToSerialize) {
        int posLength = Float.BYTES * 2;
        int uuidLength = Long.BYTES * 2;
        int flagLength = Byte.BYTES;
        return posLength + uuidLength + flagLength;
    }

    @Override
    public byte getVersion() {
        return 1;
    }
}
