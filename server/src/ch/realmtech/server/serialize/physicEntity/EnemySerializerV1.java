package ch.realmtech.server.serialize.physicEntity;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.ecs.component.ItemComponent;
import ch.realmtech.server.ecs.component.PlayerConnexionComponent;
import ch.realmtech.server.ecs.component.PositionComponent;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import ch.realmtech.server.enemy.EnemyComponent;
import ch.realmtech.server.serialize.Serializer;
import ch.realmtech.server.serialize.SerializerController;
import ch.realmtech.server.serialize.types.SerializedRawBytes;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.artemis.annotations.Wire;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.UUID;

public class EnemySerializerV1 implements Serializer<Integer, PhysicEntityArgs> {

    private ComponentMapper<PositionComponent> mPos;
    private ComponentMapper<PlayerConnexionComponent> mPlayerConnexion;
    private ComponentMapper<ItemComponent> mItem;
    private ComponentMapper<EnemyComponent> mEnemy;
    @Wire(name = "systemsAdmin")
    private SystemsAdminCommun systemsAdminCommun;
    @Override
    public SerializedRawBytes toRawBytes(World world, SerializerController serializerController, Integer enemyToSerialize) {
        ByteBuf buffer = Unpooled.buffer(getBytesSize(world, serializerController, enemyToSerialize));
        EnemyComponent enemyComponent = mEnemy.get(enemyToSerialize);

        PositionComponent positionComponent = mPos.get(enemyToSerialize);
        UUID uuid = systemsAdminCommun.getUuidEntityManager().getEntityUuid(enemyToSerialize);
        byte flag = enemyComponent.getFlag();

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
