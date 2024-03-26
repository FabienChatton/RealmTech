package ch.realmtech.server.serialize.chest;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import ch.realmtech.server.level.cell.ChestEditEntity;
import ch.realmtech.server.serialize.Serializer;
import ch.realmtech.server.serialize.SerializerController;
import ch.realmtech.server.serialize.inventory.InventoryArgs;
import ch.realmtech.server.serialize.types.SerializedRawBytes;
import com.artemis.World;
import com.artemis.annotations.Wire;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.UUID;

public class ChestSerializerV1 implements Serializer<Integer, ChestEditEntity> {
    @Wire(name = "systemsAdmin")
    private SystemsAdminCommun systemsAdminCommun;
    @Override
    public SerializedRawBytes toRawBytes(World world, SerializerController serializerController, Integer motherChestToSerializer) {
        int chestInventoryId = systemsAdminCommun.getInventoryManager().getChestInventoryId(motherChestToSerializer);
        InventoryComponent inventoryComponent = systemsAdminCommun.getInventoryManager().getChestInventory(motherChestToSerializer);
        UUID uuid = systemsAdminCommun.getUuidEntityManager().getEntityUuid(chestInventoryId);

        ByteBuf buffer = Unpooled.buffer(getBytesSize(world, serializerController, motherChestToSerializer));

        ByteBufferHelper.encodeSerializedApplicationBytes(buffer, serializerController.getUuidSerializerController(), uuid);
        ByteBufferHelper.encodeSerializedApplicationBytes(buffer, serializerController.getInventorySerializerManager(), inventoryComponent);

        return new SerializedRawBytes(buffer.array());
    }

    @Override
    public ChestEditEntity fromBytes(World world, SerializerController serializerController, SerializedRawBytes rawBytes) {
        ByteBuf buffer = Unpooled.wrappedBuffer(rawBytes.rawBytes());

        UUID uuidChest = ByteBufferHelper.decodeSerializedApplicationBytes(buffer, serializerController.getUuidSerializerController());
        InventoryArgs inventoryArgs = ByteBufferHelper.decodeSerializedApplicationBytes(buffer, serializerController.getInventorySerializerManager()).apply(world.getRegistered("itemManager"));

        return ChestEditEntity.createSetInventory(uuidChest, inventoryArgs.inventory(), inventoryArgs.numberOfSlotParRow(), inventoryArgs.numberOfRow());
    }

    @Override
    public int getBytesSize(World world, SerializerController serializerController, Integer motherChestToSerializer) {
        int inventoryId = systemsAdminCommun.getInventoryManager().getChestInventoryId(motherChestToSerializer);
        InventoryComponent inventoryComponent = systemsAdminCommun.getInventoryManager().getChestInventory(motherChestToSerializer);
        UUID uuid = systemsAdminCommun.getUuidEntityManager().getEntityUuid(inventoryId);

        int uuidLength = serializerController.getApplicationBytesLength(serializerController.getUuidSerializerController(), uuid);
        int inventoryLength = serializerController.getApplicationBytesLength(serializerController.getInventorySerializerManager(), inventoryComponent);
        // length uuid, uuid, length inventory, inventory
        return Integer.BYTES + uuidLength * Byte.BYTES + Integer.BYTES + inventoryLength * Byte.BYTES;
    }

    @Override
    public byte getVersion() {
        return 1;
    }
}
