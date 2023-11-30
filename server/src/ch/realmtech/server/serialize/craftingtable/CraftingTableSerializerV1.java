package ch.realmtech.server.serialize.craftingtable;

import ch.realmtech.server.craft.CraftingStrategyItf;
import ch.realmtech.server.ctrl.ItemManager;
import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.ecs.component.CraftingTableComponent;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.system.InventoryManager;
import ch.realmtech.server.ecs.system.UuidComponentManager;
import ch.realmtech.server.serialize.Serializer;
import ch.realmtech.server.serialize.SerializerController;
import ch.realmtech.server.serialize.inventory.InventoryArgs;
import ch.realmtech.server.serialize.types.SerializedRawBytes;
import com.artemis.World;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.UUID;
import java.util.function.Consumer;

public class CraftingTableSerializerV1 implements Serializer<CraftingTableComponent, Consumer<Integer>> {
    @Override
    public SerializedRawBytes toRawBytes(World world, SerializerController serializerController, CraftingTableComponent craftingTableToSerialize) {
        ByteBuf buffer = Unpooled.buffer(getBytesSize(world, serializerController, craftingTableToSerialize));
        UUID craftingInventoryUuid = world.getSystem(UuidComponentManager.class).getRegisteredComponent(craftingTableToSerialize.craftingInventory).getUuid();
        InventoryComponent craftingInventory = world.getSystem(InventoryManager.class).getCraftingInventory(craftingTableToSerialize);

        UUID craftingResultInventoryUuid = world.getSystem(UuidComponentManager.class).getRegisteredComponent(craftingTableToSerialize.craftingResultInventory).getUuid();
        InventoryComponent craftingResultInventory = world.getSystem(InventoryManager.class).getCraftingResultInventory(craftingTableToSerialize);


        ByteBufferHelper.encodeSerializedApplicationBytes(buffer, serializerController.getUuidSerializerController(), craftingInventoryUuid);
        ByteBufferHelper.encodeSerializedApplicationBytes(buffer, serializerController.getInventorySerializerManager(), craftingInventory);

        ByteBufferHelper.encodeSerializedApplicationBytes(buffer, serializerController.getUuidSerializerController(), craftingResultInventoryUuid);
        // serialized a empty craft result inventory
        InventoryComponent craftingResultInventoryCopyEmpty = new InventoryComponent().set(craftingResultInventory.numberOfSlotParRow, craftingInventory.numberOfRow);
        ByteBufferHelper.encodeSerializedApplicationBytes(buffer, serializerController.getInventorySerializerManager(), craftingResultInventoryCopyEmpty);

        buffer.writeByte(craftingTableToSerialize.getCraftResultStrategy().getId());
        return new SerializedRawBytes(buffer.array());
    }

    @Override
    public Consumer<Integer> fromBytes(World world, SerializerController serializerController, SerializedRawBytes rawBytes) {
        ByteBuf buffer = Unpooled.wrappedBuffer(rawBytes.rawBytes());
        ItemManager itemManager = world.getRegistered("itemManager");
        UUID craftingInventoryUuid = ByteBufferHelper.decodeSerializedApplicationBytes(buffer, serializerController.getUuidSerializerController());
        InventoryArgs craftingInventoryArgs = ByteBufferHelper.decodeSerializedApplicationBytes(buffer, serializerController.getInventorySerializerManager()).apply(itemManager);

        UUID craftingResultInventoryUuid = ByteBufferHelper.decodeSerializedApplicationBytes(buffer, serializerController.getUuidSerializerController());
        InventoryArgs craftingResultInventoryArgs = ByteBufferHelper.decodeSerializedApplicationBytes(buffer, serializerController.getInventorySerializerManager()).apply(itemManager);

        byte craftingStrategyId = buffer.readByte();

        return motherEntity -> world.getSystem(InventoryManager.class).createCraftingTable(motherEntity, craftingInventoryUuid, craftingInventoryArgs.inventory(), craftingInventoryArgs.numberOfSlotParRow(), craftingInventoryArgs.numberOfRow(), craftingResultInventoryUuid);
    }

    @Override
    public int getBytesSize(World world, SerializerController serializerController, CraftingTableComponent craftingTableToSerialize) {
        UUID craftingInventoryUuid = world.getSystem(UuidComponentManager.class).getRegisteredComponent(craftingTableToSerialize.craftingInventory).getUuid();
        InventoryComponent craftingInventory = world.getSystem(InventoryManager.class).getCraftingInventory(craftingTableToSerialize);
        UUID craftingResultInventoryUuid = world.getSystem(UuidComponentManager.class).getRegisteredComponent(craftingTableToSerialize.craftingResultInventory).getUuid();
        InventoryComponent craftingResultInventory = world.getSystem(InventoryManager.class).getCraftingResultInventory(craftingTableToSerialize);

        int craftingInventoryUuidLength = serializerController.getApplicationBytesLength(serializerController.getUuidSerializerController(), craftingInventoryUuid);
        int craftingInventoryLength = serializerController.getApplicationBytesLength(serializerController.getInventorySerializerManager(), craftingInventory);

        int craftingResultInventoryUuidLength = serializerController.getApplicationBytesLength(serializerController.getUuidSerializerController(), craftingResultInventoryUuid);
        int craftingResultInventoryLength = serializerController.getApplicationBytesLength(serializerController.getInventorySerializerManager(), craftingResultInventory);

        byte craftingStrategyIdLength = CraftingStrategyItf.ID_LENGTH;
        return craftingInventoryUuidLength + craftingInventoryLength + craftingResultInventoryUuidLength + /* craftingResultInventoryLength + */ craftingStrategyIdLength;
    }

    @Override
    public byte getVersion() {
        return 1;
    }
}
