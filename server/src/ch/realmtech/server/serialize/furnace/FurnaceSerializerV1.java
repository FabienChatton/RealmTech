package ch.realmtech.server.serialize.furnace;

import ch.realmtech.server.ctrl.ItemManager;
import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.ecs.component.CraftingTableComponent;
import ch.realmtech.server.ecs.component.FurnaceComponent;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.level.cell.FurnaceEditEntity;
import ch.realmtech.server.serialize.Serializer;
import ch.realmtech.server.serialize.SerializerController;
import ch.realmtech.server.serialize.inventory.InventoryArgs;
import ch.realmtech.server.serialize.types.SerializedRawBytes;
import ch.realmtech.server.uuid.UuidSupplierOrRandom;
import com.artemis.ComponentMapper;
import com.artemis.World;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.UUID;

public class FurnaceSerializerV1 implements Serializer<Integer, FurnaceEditEntity> {
    private ComponentMapper<FurnaceComponent> mFurnace;
    private ComponentMapper<CraftingTableComponent> mCraftingTable;
    private ComponentMapper<InventoryComponent> mInventory;
    @Override
    public SerializedRawBytes toRawBytes(World world, SerializerController serializerController, Integer furnaceToSerialize) {
        ByteBuf buffer = Unpooled.buffer(getBytesSize(world, serializerController, furnaceToSerialize));

        SystemsAdminServer systemsAdminServer = world.getRegistered(SystemsAdminServer.class);
        CraftingTableComponent craftingTableComponent = mCraftingTable.get(furnaceToSerialize);
        FurnaceComponent furnaceComponent = mFurnace.get(furnaceToSerialize);

        UUID furnaceUuid = systemsAdminServer.uuidEntityManager.getEntityUuid(furnaceToSerialize);

        InventoryComponent craftingInventoryComponent = mInventory.get(craftingTableComponent.craftingInventory);
        UUID craftingInventoryUuid = systemsAdminServer.uuidEntityManager.getEntityUuid(craftingTableComponent.craftingInventory);

        InventoryComponent craftingResultInventoryComponent = mInventory.get(craftingTableComponent.craftingResultInventory);
        UUID craftingResultInventoryUuid = systemsAdminServer.uuidEntityManager.getEntityUuid(craftingTableComponent.craftingResultInventory);

        InventoryComponent carburantInventoryComponent = mInventory.get(furnaceComponent.inventoryCarburant);
        UUID carburantInventoryUuid = systemsAdminServer.uuidEntityManager.getEntityUuid(furnaceComponent.inventoryCarburant);


        ByteBufferHelper.encodeSerializedApplicationBytes(buffer, serializerController.getUuidSerializerController(), furnaceUuid);

        ByteBufferHelper.encodeSerializedApplicationBytes(buffer, serializerController.getInventorySerializerManager(), craftingInventoryComponent);
        ByteBufferHelper.encodeSerializedApplicationBytes(buffer, serializerController.getUuidSerializerController(), craftingInventoryUuid);

        ByteBufferHelper.encodeSerializedApplicationBytes(buffer, serializerController.getInventorySerializerManager(), craftingResultInventoryComponent);
        ByteBufferHelper.encodeSerializedApplicationBytes(buffer, serializerController.getUuidSerializerController(), craftingResultInventoryUuid);

        ByteBufferHelper.encodeSerializedApplicationBytes(buffer, serializerController.getInventorySerializerManager(), carburantInventoryComponent);
        ByteBufferHelper.encodeSerializedApplicationBytes(buffer, serializerController.getUuidSerializerController(), carburantInventoryUuid);
        return new SerializedRawBytes(buffer.array());
    }

    @Override
    public FurnaceEditEntity fromBytes(World world, SerializerController serializerController, SerializedRawBytes rawBytes) {
        ByteBuf buffer = Unpooled.wrappedBuffer(rawBytes.rawBytes());
        ItemManager itemManager = world.getRegistered("itemManager");

        UUID furnaceUuid = ByteBufferHelper.decodeSerializedApplicationBytes(buffer, serializerController.getUuidSerializerController());

        InventoryArgs craftingInventoryArgs = ByteBufferHelper.decodeSerializedApplicationBytes(buffer, serializerController.getInventorySerializerManager()).apply(itemManager);
        UUID craftingInventoryUuid = ByteBufferHelper.decodeSerializedApplicationBytes(buffer, serializerController.getUuidSerializerController());

        InventoryArgs craftingResultInventoryArgs = ByteBufferHelper.decodeSerializedApplicationBytes(buffer, serializerController.getInventorySerializerManager()).apply(itemManager);
        UUID craftingResultInventoryUuid = ByteBufferHelper.decodeSerializedApplicationBytes(buffer, serializerController.getUuidSerializerController());

        InventoryArgs carburentInventoryArgs = ByteBufferHelper.decodeSerializedApplicationBytes(buffer, serializerController.getInventorySerializerManager()).apply(itemManager);
        UUID carburantInventoryUuid = ByteBufferHelper.decodeSerializedApplicationBytes(buffer, serializerController.getUuidSerializerController());

        return new FurnaceEditEntity(new UuidSupplierOrRandom(furnaceUuid), new UuidSupplierOrRandom(craftingInventoryUuid), craftingInventoryArgs.inventory(), new UuidSupplierOrRandom(carburantInventoryUuid), carburentInventoryArgs.inventory(), new UuidSupplierOrRandom(craftingResultInventoryUuid), craftingResultInventoryArgs.inventory());
    }

    @Override
    public int getBytesSize(World world, SerializerController serializerController, Integer furnaceToSerialize) {
        SystemsAdminServer systemsAdminServer = world.getRegistered(SystemsAdminServer.class);
        CraftingTableComponent craftingTableComponent = mCraftingTable.get(furnaceToSerialize);
        FurnaceComponent furnaceComponent = mFurnace.get(furnaceToSerialize);

        UUID furnaceUuid = systemsAdminServer.uuidEntityManager.getEntityUuid(furnaceToSerialize);

        InventoryComponent craftingInventoryComponent = mInventory.get(craftingTableComponent.craftingInventory);
        UUID craftingInventoryUuid = systemsAdminServer.uuidEntityManager.getEntityUuid(craftingTableComponent.craftingInventory);

        InventoryComponent craftingResultInventoryComponent = mInventory.get(craftingTableComponent.craftingResultInventory);
        UUID craftingResultInventoryUuid = systemsAdminServer.uuidEntityManager.getEntityUuid(craftingTableComponent.craftingResultInventory);

        InventoryComponent carburantInventoryComponent = mInventory.get(furnaceComponent.inventoryCarburant);
        UUID carburantInventoryUuid = systemsAdminServer.uuidEntityManager.getEntityUuid(furnaceComponent.inventoryCarburant);

        int furnaceUuidLength = serializerController.getApplicationBytesLength(serializerController.getUuidSerializerController(), furnaceUuid);

        int craftingInventoryComponentLength = serializerController.getApplicationBytesLength(serializerController.getInventorySerializerManager(), craftingInventoryComponent);
        int craftingInventoryUuidLength = serializerController.getApplicationBytesLength(serializerController.getUuidSerializerController(), craftingInventoryUuid);

        int craftingResultInventoryLength = serializerController.getApplicationBytesLength(serializerController.getInventorySerializerManager(), craftingResultInventoryComponent);
        int craftingResultInventoryUuidLength = serializerController.getApplicationBytesLength(serializerController.getUuidSerializerController(), craftingResultInventoryUuid);

        int carburantInventoryLength = serializerController.getApplicationBytesLength(serializerController.getInventorySerializerManager(), carburantInventoryComponent);
        int carburantInventoryUuidLength = serializerController.getApplicationBytesLength(serializerController.getUuidSerializerController(), carburantInventoryUuid);

        return furnaceUuidLength + craftingInventoryComponentLength + craftingInventoryUuidLength + craftingResultInventoryLength + craftingResultInventoryUuidLength + carburantInventoryLength + carburantInventoryUuidLength;
    }

    @Override
    public byte getVersion() {
        return 1;
    }
}
