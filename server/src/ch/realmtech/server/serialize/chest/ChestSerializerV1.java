package ch.realmtech.server.serialize.chest;

import ch.realmtech.server.ctrl.ItemManager;
import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.component.UuidComponent;
import ch.realmtech.server.ecs.system.InventoryManager;
import ch.realmtech.server.ecs.system.UuidComponentManager;
import ch.realmtech.server.serialize.Serializer;
import ch.realmtech.server.serialize.SerializerController;
import ch.realmtech.server.serialize.inventory.InventoryArgs;
import ch.realmtech.server.serialize.types.SerializedApplicationBytes;
import ch.realmtech.server.serialize.types.SerializedRawBytes;
import com.artemis.World;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class ChestSerializerV1 implements Serializer<Integer, Consumer<Integer>> {
    @Override
    public SerializedRawBytes toRawBytes(World world, SerializerController serializerController, Integer motherChestToSerializer) {
        int chestInventoryId = world.getSystem(InventoryManager.class).getChestInventoryId(motherChestToSerializer);
        InventoryComponent inventoryComponent = world.getSystem(InventoryManager.class).getChestInventory(motherChestToSerializer);
        UuidComponent uuidComponent = world.getSystem(UuidComponentManager.class).getRegisteredComponent(chestInventoryId);

        ByteBuf buffer = Unpooled.buffer(getBytesSize(world, serializerController, motherChestToSerializer));

        ByteBufferHelper.encodeSerializedApplicationBytes(buffer, serializerController.getUuidSerializerController(), uuidComponent.getUuid());
        ByteBufferHelper.encodeSerializedApplicationBytes(buffer, serializerController.getInventorySerializerManager(), inventoryComponent);

        return new SerializedRawBytes(buffer.array());
    }

    @Override
    public Consumer<Integer> fromBytes(World world, SerializerController serializerController, SerializedRawBytes rawBytes) {
        ByteBuf buffer = Unpooled.wrappedBuffer(rawBytes.rawBytes());

        UUID uuidChest = ByteBufferHelper.decodeSerializedApplicationBytes(buffer, serializerController.getUuidSerializerController());
        InventoryArgs inventoryArgs = ByteBufferHelper.decodeSerializedApplicationBytes(buffer, serializerController.getInventorySerializerManager()).apply(world.getRegistered("itemManager"));

        return mother -> world.getSystem(InventoryManager.class).createChest(mother, inventoryArgs.inventory(), uuidChest, inventoryArgs.numberOfSlotParRow(), inventoryArgs.numberOfRow());
    }

    @Override
    public int getBytesSize(World world, SerializerController serializerController, Integer motherChestToSerializer) {
        int inventoryId = world.getSystem(InventoryManager.class).getChestInventoryId(motherChestToSerializer);
        InventoryComponent inventoryComponent = world.getSystem(InventoryManager.class).getChestInventory(motherChestToSerializer);
        UuidComponent uuidComponent = world.getSystem(UuidComponentManager.class).getRegisteredComponent(inventoryId);

        int uuidLength = serializerController.getApplicationBytesLength(serializerController.getUuidSerializerController(), uuidComponent.getUuid());
        int inventoryLength = serializerController.getApplicationBytesLength(serializerController.getInventorySerializerManager(), inventoryComponent);
        // length uuid, uuid, length inventory, inventory
        return Integer.BYTES + uuidLength * Byte.BYTES + Integer.BYTES + inventoryLength * Byte.BYTES;
    }

    @Override
    public byte getVersion() {
        return 1;
    }
}
