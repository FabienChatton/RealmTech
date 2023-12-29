package ch.realmtech.server.serialize.player;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.ecs.component.LifeComponent;
import ch.realmtech.server.ecs.component.PositionComponent;
import ch.realmtech.server.level.cell.ChestEditEntity;
import ch.realmtech.server.serialize.Serializer;
import ch.realmtech.server.serialize.SerializerController;
import ch.realmtech.server.serialize.life.LifeArgs;
import ch.realmtech.server.serialize.types.SerializedApplicationBytes;
import ch.realmtech.server.serialize.types.SerializedRawBytes;
import com.artemis.ComponentMapper;
import com.artemis.World;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.function.Consumer;

public class PlayerSerializerV2 implements Serializer<PlayerSerializerConfig, Consumer<Integer>> {

    private ComponentMapper<PositionComponent> mPos;
    private ComponentMapper<LifeComponent> mLife;
    @Override
    public SerializedRawBytes toRawBytes(World world, SerializerController serializerController, PlayerSerializerConfig playerToSerializeConfig) {
        int playerToSerialize = playerToSerializeConfig.getPlayerId();
        ByteBuf buffer = Unpooled.buffer(getBytesSize(world, serializerController, playerToSerializeConfig));

        PositionComponent positionComponent = mPos.get(playerToSerialize);
        LifeComponent lifeComponent = mLife.get(playerToSerialize);


        buffer.writeByte(playerToSerializeConfig.toByte());
        if (playerToSerializeConfig.isWriteInventory()) {
            SerializedApplicationBytes chestInventoryEncode = serializerController.getChestSerializerController().encode(playerToSerialize);
            ByteBufferHelper.writeSerializedApplicationBytes(buffer, chestInventoryEncode);
        }
        buffer.writeFloat(positionComponent.x);
        buffer.writeFloat(positionComponent.y);
        ByteBufferHelper.encodeSerializedApplicationBytes(buffer, serializerController.getLifeSerializerController(), lifeComponent);

        return new SerializedRawBytes(buffer.array());
    }

    @Override
    public Consumer<Integer> fromBytes(World world, SerializerController serializerController, SerializedRawBytes rawBytes) {
        ByteBuf buffer = Unpooled.wrappedBuffer(rawBytes.rawBytes());

        byte playerSerializerConfigByte = buffer.readByte();
        PlayerSerializerConfig playerSerializerConfig = new PlayerSerializerConfig(playerSerializerConfigByte);

        ChestEditEntity playerChestEditEntity;
        if (playerSerializerConfig.isWriteInventory()) {
             playerChestEditEntity = ByteBufferHelper.decodeSerializedApplicationBytes(buffer, serializerController.getChestSerializerController());
        } else {
            playerChestEditEntity = null;
        }

        float posX = buffer.readFloat();
        float posY = buffer.readFloat();

        LifeArgs lifeArgs = ByteBufferHelper.decodeSerializedApplicationBytes(buffer, serializerController.getLifeSerializerController());

        return (playerId) -> {
            if (playerChestEditEntity != null) {
                playerChestEditEntity.editEntity(world.getRegistered("executeOnContext"), playerId);
            }

            mPos.create(playerId).set(posX, posY);
            mLife.create(playerId).set(lifeArgs.heart());
        };
    }

    @Override
    public int getBytesSize(World world, SerializerController serializerController, PlayerSerializerConfig playerToSerializeConfig) {
        int playerToSerialize = playerToSerializeConfig.getPlayerId();

        int configLength = Byte.BYTES;
        int chestInventoryLength;
        if (playerToSerializeConfig.isWriteInventory()) {
            chestInventoryLength = serializerController.getApplicationBytesLength(serializerController.getChestSerializerController(), playerToSerialize) + 2;
        } else {
            chestInventoryLength = 0;
        }
        int posLength = Float.BYTES * 2;
        int lifeLength = serializerController.getApplicationBytesLength(serializerController.getLifeSerializerController(), mLife.get(playerToSerialize)) + 2;

        return configLength + chestInventoryLength + posLength + lifeLength;
    }

    @Override
    public byte getVersion() {
        return 2;
    }
}
