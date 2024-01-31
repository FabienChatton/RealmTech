package ch.realmtech.server.serialize.player;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.ecs.component.PositionComponent;
import ch.realmtech.server.ecs.system.UuidComponentManager;
import ch.realmtech.server.level.cell.ChestEditEntity;
import ch.realmtech.server.serialize.Serializer;
import ch.realmtech.server.serialize.SerializerController;
import ch.realmtech.server.serialize.types.SerializedApplicationBytes;
import ch.realmtech.server.serialize.types.SerializedRawBytes;
import com.artemis.ComponentMapper;
import com.artemis.World;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.UUID;
import java.util.function.Consumer;

public class PlayerSerializerV1 implements Serializer<PlayerSerializerConfig, Consumer<Integer>> {

    private ComponentMapper<PositionComponent> mPos;
    @Override
    public SerializedRawBytes toRawBytes(World world, SerializerController serializerController, PlayerSerializerConfig playerToSerializeConfig) {
        int playerToSerialize = playerToSerializeConfig.getPlayerId();
        ByteBuf buffer = Unpooled.buffer(getBytesSize(world, serializerController, playerToSerializeConfig));

        PositionComponent positionComponent = mPos.get(playerToSerialize);

        SerializedApplicationBytes chestInventoryEncode = serializerController.getChestSerializerController().encode(playerToSerialize);

        ByteBufferHelper.writeSerializedApplicationBytes(buffer, chestInventoryEncode);
        buffer.writeFloat(positionComponent.x);
        buffer.writeFloat(positionComponent.y);

        return new SerializedRawBytes(buffer.array());
    }

    @Override
    public Consumer<Integer> fromBytes(World world, SerializerController serializerController, SerializedRawBytes rawBytes) {
        ByteBuf buffer = Unpooled.wrappedBuffer(rawBytes.rawBytes());

        ChestEditEntity playerChestEditEntity = ByteBufferHelper.decodeSerializedApplicationBytes(buffer, serializerController.getChestSerializerController());
        float posX = buffer.readFloat();
        float posY = buffer.readFloat();

        return playerId -> {
            playerChestEditEntity.createEntity(world.getRegistered("executeOnContext"), playerId);

            world.edit(playerId).create(PositionComponent.class).set(posX, posY);
        };
    }

    @Override
    public int getBytesSize(World world, SerializerController serializerController, PlayerSerializerConfig playerToSerializeConfig) {
        int playerToSerialize = playerToSerializeConfig.getPlayerId();
        UUID playerUuid = world.getSystem(UuidComponentManager.class).getRegisteredComponent(playerToSerialize).getUuid();

        int chestInventoryLength = serializerController.getApplicationBytesLength(serializerController.getChestSerializerController(), playerToSerialize);
        int playerUuidLength = serializerController.getApplicationBytesLength(serializerController.getUuidSerializerController(), playerUuid);
        int posLength = Float.BYTES * 2;

        return playerUuidLength + chestInventoryLength + posLength;
    }

    @Override
    public byte getVersion() {
        return 1;
    }
}
