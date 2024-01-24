package ch.realmtech.server.serialize.energyBattery;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.ecs.component.EnergyBatteryComponent;
import ch.realmtech.server.ecs.component.FaceComponent;
import ch.realmtech.server.energy.EnergyBatteryEditEntity;
import ch.realmtech.server.serialize.Serializer;
import ch.realmtech.server.serialize.SerializerController;
import ch.realmtech.server.serialize.types.SerializedRawBytes;
import com.artemis.ComponentMapper;
import com.artemis.World;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class EnergyBatteryV1 implements Serializer<Integer, EnergyBatteryEditEntity> {
    private ComponentMapper<EnergyBatteryComponent> mEnergyBattery;
    private ComponentMapper<FaceComponent> mFace;
    @Override
    public SerializedRawBytes toRawBytes(World world, SerializerController serializerController, Integer energyBatteryToSerialize) {
        ByteBuf buffer = Unpooled.buffer(getBytesSize(world, serializerController, energyBatteryToSerialize));
        EnergyBatteryComponent energyBatteryComponent = mEnergyBattery.get(energyBatteryToSerialize);
        FaceComponent faceComponent = mFace.get(energyBatteryToSerialize);

        buffer.writeLong(energyBatteryComponent.getStored());
        buffer.writeLong(energyBatteryComponent.getCapacity());

        ByteBufferHelper.encodeSerializedApplicationBytes(buffer, serializerController.getFaceSerializerController(), faceComponent);
        buffer.writeByte(faceComponent.getFlags());

        return new SerializedRawBytes(buffer.array());
    }

    @Override
    public EnergyBatteryEditEntity fromBytes(World world, SerializerController serializerController, SerializedRawBytes rawBytes) {
        ByteBuf buffer = Unpooled.wrappedBuffer(rawBytes.rawBytes());

        long stored = buffer.readLong();
        long capacity = buffer.readLong();

        byte face = ByteBufferHelper.decodeSerializedApplicationBytes(buffer, serializerController.getFaceSerializerController());
        return new EnergyBatteryEditEntity(stored, capacity, face);
    }

    @Override
    public int getBytesSize(World world, SerializerController serializerController, Integer energyBatteryToSerialize) {
        FaceComponent faceComponent = mFace.get(energyBatteryToSerialize);
        int storedLength = Long.BYTES;
        int capacityLength = Long.BYTES;
        int faceBytesLength = serializerController.getApplicationBytesLength(serializerController.getFaceSerializerController(), faceComponent);
        return storedLength + capacityLength + faceBytesLength;
    }

    @Override
    public byte getVersion() {
        return 1;
    }
}
