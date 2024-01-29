package ch.realmtech.server.serialize.energyGenerator;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.ecs.component.EnergyBatteryComponent;
import ch.realmtech.server.ecs.component.EnergyGeneratorComponent;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.component.UuidComponent;
import ch.realmtech.server.energy.EnergyGeneratorEditEntity;
import ch.realmtech.server.serialize.Serializer;
import ch.realmtech.server.serialize.SerializerController;
import ch.realmtech.server.serialize.inventory.InventoryArgs;
import ch.realmtech.server.serialize.types.SerializedRawBytes;
import com.artemis.ComponentMapper;
import com.artemis.World;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.UUID;

public class EnergyGeneratorSerializerV1 implements Serializer<Integer, EnergyGeneratorEditEntity> {
    private ComponentMapper<EnergyGeneratorComponent> mEnergyGenerator;
    private ComponentMapper<EnergyBatteryComponent> mEnergyBattery;
    private ComponentMapper<InventoryComponent> mInventory;
    private ComponentMapper<UuidComponent> mUuid;

    @Override
    public SerializedRawBytes toRawBytes(World world, SerializerController serializerController, Integer energyGeneratorToSerialize) {
        ByteBuf buffer = Unpooled.buffer(getBytesSize(world, serializerController, energyGeneratorToSerialize));
        InventoryComponent inventoryCarburantComponent = mInventory.get(energyGeneratorToSerialize);
        EnergyBatteryComponent energyBatteryComponent = mEnergyBattery.get(energyGeneratorToSerialize);

        UUID carburantInventoryUuid = mUuid.get(energyGeneratorToSerialize).getUuid();
        ByteBufferHelper.encodeSerializedApplicationBytes(buffer, serializerController.getUuidSerializerController(), carburantInventoryUuid);
        ByteBufferHelper.encodeSerializedApplicationBytes(buffer, serializerController.getInventorySerializerManager(), inventoryCarburantComponent);
        buffer.writeLong(energyBatteryComponent.getStored());
        buffer.writeLong(energyBatteryComponent.getCapacity());
        return new SerializedRawBytes(buffer.array());
    }

    @Override
    public EnergyGeneratorEditEntity fromBytes(World world, SerializerController serializerController, SerializedRawBytes rawBytes) {
        ByteBuf buffer = Unpooled.wrappedBuffer(rawBytes.rawBytes());
        UUID carburantInventoryUuid = ByteBufferHelper.decodeSerializedApplicationBytes(buffer, serializerController.getUuidSerializerController());
        InventoryArgs inventoryArgs = ByteBufferHelper.decodeSerializedApplicationBytes(buffer, serializerController.getInventorySerializerManager()).apply(world.getRegistered("itemManager"));

        long stored = buffer.readLong();
        long capacity = buffer.readLong();
        return new EnergyGeneratorEditEntity(carburantInventoryUuid, inventoryArgs.inventory(), inventoryArgs.numberOfRow(), inventoryArgs.numberOfSlotParRow(), stored, capacity);
    }

    @Override
    public int getBytesSize(World world, SerializerController serializerController, Integer energyGeneratorToSerialize) {
        InventoryComponent inventoryCarburantComponent = mInventory.get(energyGeneratorToSerialize);

        int inventoryCarburantUuidLength = Long.BYTES * 2;
        int inventoryCarburantLength = serializerController.getApplicationBytesLength(serializerController.getInventorySerializerManager(), inventoryCarburantComponent);
        int energyBatteryStoreLength = Long.BYTES * 2;
        return inventoryCarburantUuidLength + inventoryCarburantLength + energyBatteryStoreLength;
    }

    @Override
    public byte getVersion() {
        return 1;
    }
}
