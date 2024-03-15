package ch.realmtech.server.serialize.craftingtable;

import ch.realmtech.server.ctrl.ItemManager;
import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.ecs.component.CraftingTableComponent;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.system.InventoryManager;
import ch.realmtech.server.ecs.system.UuidEntityManager;
import ch.realmtech.server.level.cell.CraftingTableEditEntity;
import ch.realmtech.server.newMod.entityEditFactory.EditEntityFactory;
import ch.realmtech.server.newRegistry.NewRegistry;
import ch.realmtech.server.newRegistry.RegistryUtils;
import ch.realmtech.server.serialize.Serializer;
import ch.realmtech.server.serialize.SerializerController;
import ch.realmtech.server.serialize.inventory.InventoryArgs;
import ch.realmtech.server.serialize.types.SerializedRawBytes;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.artemis.annotations.Wire;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.Optional;
import java.util.UUID;

public class CraftingTableSerializerV2 implements Serializer<Integer, CraftingTableEditEntity> {
    @Wire(name = "rootRegistry")
    private NewRegistry<?> rootRegistry;
    private ComponentMapper<CraftingTableComponent> mCraftingTable;

    @Override
    public SerializedRawBytes toRawBytes(World world, SerializerController serializerController, Integer craftingTableToSerializeId) {
        CraftingTableComponent craftingTableToSerialize = mCraftingTable.get(craftingTableToSerializeId);

        ByteBuf buffer = Unpooled.buffer(getBytesSize(world, serializerController, craftingTableToSerializeId));
        UUID craftingInventoryUuid = world.getSystem(UuidEntityManager.class).getEntityUuid(craftingTableToSerialize.craftingInventory);
        InventoryComponent craftingInventory = world.getSystem(InventoryManager.class).getCraftingInventory(craftingTableToSerialize);

        UUID craftingResultInventoryUuid = world.getSystem(UuidEntityManager.class).getEntityUuid(craftingTableToSerialize.craftingResultInventory);

        ByteBufferHelper.encodeSerializedApplicationBytes(buffer, serializerController.getUuidSerializerController(), craftingInventoryUuid);
        ByteBufferHelper.encodeSerializedApplicationBytes(buffer, serializerController.getInventorySerializerManager(), craftingInventory);

        ByteBufferHelper.encodeSerializedApplicationBytes(buffer, serializerController.getUuidSerializerController(), craftingResultInventoryUuid);

        return new SerializedRawBytes(buffer.array());
    }

    @Override
    public CraftingTableEditEntity fromBytes(World world, SerializerController serializerController, SerializedRawBytes rawBytes) {
        ByteBuf buffer = Unpooled.wrappedBuffer(rawBytes.rawBytes());
        ItemManager itemManager = world.getRegistered("itemManager");
        UUID craftingInventoryUuid = ByteBufferHelper.decodeSerializedApplicationBytes(buffer, serializerController.getUuidSerializerController());
        InventoryArgs craftingInventoryArgs = ByteBufferHelper.decodeSerializedApplicationBytes(buffer, serializerController.getInventorySerializerManager()).apply(itemManager);

        UUID craftingResultInventoryUuid = ByteBufferHelper.decodeSerializedApplicationBytes(buffer, serializerController.getUuidSerializerController());

        Optional<EditEntityFactory> editEntityFactory = RegistryUtils.findEntry(rootRegistry, EditEntityFactory.class);
        return editEntityFactory.orElseThrow().createSetCraftingTable(craftingInventoryUuid, craftingInventoryArgs.inventory(), craftingInventoryArgs.numberOfSlotParRow(), craftingInventoryArgs.numberOfRow(), craftingResultInventoryUuid);
    }

    @Override
    public int getBytesSize(World world, SerializerController serializerController, Integer craftingTableToSerializeId) {
        CraftingTableComponent craftingTableToSerialize = mCraftingTable.get(craftingTableToSerializeId);
        UUID craftingInventoryUuid = world.getSystem(UuidEntityManager.class).getEntityUuid(craftingTableToSerialize.craftingInventory);
        InventoryComponent craftingInventory = world.getSystem(InventoryManager.class).getCraftingInventory(craftingTableToSerialize);
        UUID craftingResultInventoryUuid = world.getSystem(UuidEntityManager.class).getEntityUuid(craftingTableToSerialize.craftingResultInventory);

        int craftingInventoryUuidLength = serializerController.getApplicationBytesLength(serializerController.getUuidSerializerController(), craftingInventoryUuid);
        int craftingInventoryLength = serializerController.getApplicationBytesLength(serializerController.getInventorySerializerManager(), craftingInventory);
        int craftingResultInventoryUuidLength = serializerController.getApplicationBytesLength(serializerController.getUuidSerializerController(), craftingResultInventoryUuid);

        return craftingInventoryUuidLength + craftingInventoryLength + craftingResultInventoryUuidLength;
    }

    @Override
    public byte getVersion() {
        return 2;
    }
}
