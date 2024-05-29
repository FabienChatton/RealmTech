package ch.realmtech.server.serialize.physicEntity;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.ecs.component.MobComponent;
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

public class EnemySerializerV1 implements Serializer<Integer, EnemyArgs> {

    private ComponentMapper<PositionComponent> mPos;
    private ComponentMapper<EnemyComponent> mEnemy;
    private ComponentMapper<MobComponent> mMob;
    @Wire(name = "systemsAdmin")
    private SystemsAdminCommun systemsAdminCommun;
    @Override
    public SerializedRawBytes toRawBytes(World world, SerializerController serializerController, Integer enemyToSerialize) {
        ByteBuf buffer = Unpooled.buffer(getBytesSize(world, serializerController, enemyToSerialize));
        EnemyComponent enemyComponent = mEnemy.get(enemyToSerialize);
        MobComponent mobComponent = mMob.get(enemyToSerialize);

        PositionComponent positionComponent = mPos.get(enemyToSerialize);
        UUID uuid = systemsAdminCommun.getUuidEntityManager().getEntityUuid(enemyToSerialize);

        ByteBufferHelper.writeUUID(buffer, uuid);
        buffer.writeFloat(positionComponent.x);
        buffer.writeFloat(positionComponent.y);
        buffer.writeInt(mobComponent.getMobEntry().getId());
        return new SerializedRawBytes(buffer.array());
    }

    @Override
    public EnemyArgs fromBytes(World world, SerializerController serializerController, SerializedRawBytes rawBytes) {
        ByteBuf buffer = Unpooled.wrappedBuffer(rawBytes.rawBytes());

        UUID uuid = ByteBufferHelper.readUUID(buffer);
        float x = buffer.readFloat();
        float y = buffer.readFloat();
        int enemyEntryId = buffer.readInt();
        return new EnemyArgs(uuid, x, y, enemyEntryId);
    }

    @Override
    public int getBytesSize(World world, SerializerController serializerController, Integer physicEntityToSerialize) {
        int posLength = Float.BYTES * 2;
        int uuidLength = Long.BYTES * 2;
        int enemyEntryIdLength = Integer.BYTES;
        return posLength + uuidLength + enemyEntryIdLength;
    }

    @Override
    public byte getVersion() {
        return 1;
    }
}
