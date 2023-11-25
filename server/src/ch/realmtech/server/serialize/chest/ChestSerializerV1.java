package ch.realmtech.server.serialize.chest;

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

import java.nio.ByteBuffer;
import java.util.UUID;

public class ChestSerializerV1 implements Serializer<Integer, Integer> {
    @Override
    public SerializedRawBytes toRawBytes(World world, SerializerController serializerController, Integer motherChestToSerializer) {
        int chestInventoryId = world.getSystem(InventoryManager.class).getChestInventoryId(motherChestToSerializer);
        InventoryComponent inventoryComponent = world.getSystem(InventoryManager.class).getChestInventory(motherChestToSerializer);
        UuidComponent uuidComponent = world.getSystem(UuidComponentManager.class).getRegisteredComponent(chestInventoryId);

        SerializedApplicationBytes uuidApplicationBytes = serializerController.getUuidSerializerController().encode(uuidComponent.getUuid());
        SerializedApplicationBytes inventoryApplicationBytes = serializerController.getInventorySerializerManager().encode(inventoryComponent);

        ByteBuffer byteBuffer = ByteBuffer.allocate(getBytesSize(world, serializerController, motherChestToSerializer));

        byteBuffer.putInt(uuidApplicationBytes.getLength());
        byteBuffer.put(uuidApplicationBytes.applicationBytes());

        byteBuffer.putInt(inventoryApplicationBytes.getLength());
        byteBuffer.put(inventoryApplicationBytes.applicationBytes());
        return new SerializedRawBytes(byteBuffer.array());
    }

    @Override
    public Integer fromBytes(World world, SerializerController serializerController, SerializedRawBytes rawBytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(rawBytes.rawBytes());

        int lengthUuid = byteBuffer.getInt();
        SerializedApplicationBytes uuidApplicationBytes = new SerializedApplicationBytes(new byte[lengthUuid]);
        byteBuffer.get(uuidApplicationBytes.applicationBytes());

        int lengthInventory = byteBuffer.getInt();
        SerializedApplicationBytes inventoryApplicationBytes = new SerializedApplicationBytes(new byte[lengthInventory]);
        byteBuffer.get(inventoryApplicationBytes.applicationBytes());


        UUID uuidChest = serializerController.getUuidSerializerController().decode(uuidApplicationBytes);
        InventoryArgs inventoryArgs = serializerController.getInventorySerializerManager().decode(inventoryApplicationBytes).apply(world.getRegistered("itemManager"));

        int mother = world.create();
        world.getSystem(InventoryManager.class).createChest(mother, inventoryArgs.inventory(), uuidChest, inventoryArgs.numberOfSlotParRow(), inventoryArgs.numberOfRow());
        return mother;
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
