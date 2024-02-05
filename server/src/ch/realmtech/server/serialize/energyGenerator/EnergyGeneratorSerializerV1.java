package ch.realmtech.server.serialize.energyGenerator;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.ecs.component.EnergyBatteryComponent;
import ch.realmtech.server.ecs.component.EnergyGeneratorComponent;
import ch.realmtech.server.ecs.component.UuidComponent;
import ch.realmtech.server.energy.EnergyGeneratorEditEntity;
import ch.realmtech.server.serialize.Serializer;
import ch.realmtech.server.serialize.SerializerController;
import ch.realmtech.server.serialize.types.SerializedRawBytes;
import com.artemis.ComponentMapper;
import com.artemis.World;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.UUID;

public class EnergyGeneratorSerializerV1 implements Serializer<Integer, EnergyGeneratorEditEntity> {
    private ComponentMapper<EnergyGeneratorComponent> mEnergyGenerator;
    private ComponentMapper<EnergyBatteryComponent> mEnergyBattery;
    private ComponentMapper<UuidComponent> mUuid;

    @Override
    public SerializedRawBytes toRawBytes(World world, SerializerController serializerController, Integer energyGeneratorToSerialize) {
        ByteBuf buffer = Unpooled.buffer(getBytesSize(world, serializerController, energyGeneratorToSerialize));
        EnergyBatteryComponent energyBatteryComponent = mEnergyBattery.get(energyGeneratorToSerialize);
        EnergyGeneratorComponent energyGeneratorComponent = mEnergyGenerator.get(energyGeneratorToSerialize);
        UUID energyGeneratorUuid = mUuid.get(energyGeneratorToSerialize).getUuid();

        ByteBufferHelper.writeUUID(buffer, energyGeneratorUuid);
        buffer.writeInt(energyGeneratorComponent.getRemainingTickToBurn());

        buffer.writeLong(energyBatteryComponent.getStored());
        buffer.writeLong(energyBatteryComponent.getCapacity());
        return new SerializedRawBytes(buffer.array());
    }

    @Override
    public EnergyGeneratorEditEntity fromBytes(World world, SerializerController serializerController, SerializedRawBytes rawBytes) {
        ByteBuf buffer = Unpooled.wrappedBuffer(rawBytes.rawBytes());

        UUID energyGeneratorUuid = ByteBufferHelper.readUUID(buffer);
        int remainingTickToBurn = buffer.readInt();
        long stored = buffer.readLong();
        long capacity = buffer.readLong();
        return new EnergyGeneratorEditEntity(energyGeneratorUuid, remainingTickToBurn, stored, capacity);
    }

    @Override
    public int getBytesSize(World world, SerializerController serializerController, Integer energyGeneratorToSerialize) {
        int uuidLength = Long.BYTES * 2;
        int remainingTickToBurnLength = Integer.BYTES;
        int energyBatteryStoreLength = Long.BYTES * 2;
        return uuidLength + remainingTickToBurnLength + energyBatteryStoreLength;
    }

    @Override
    public byte getVersion() {
        return 1;
    }
}
