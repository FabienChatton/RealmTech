package ch.realmtech.server.serialize.energyBattery;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.ecs.component.EnergyBatteryComponent;
import ch.realmtech.server.ecs.component.FaceComponent;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import ch.realmtech.server.energy.EnergyBatteryEditEntity;
import ch.realmtech.server.serialize.Serializer;
import ch.realmtech.server.serialize.SerializerController;
import ch.realmtech.server.serialize.types.SerializedRawBytes;
import ch.realmtech.server.uuid.UuidSupplierOrRandom;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.artemis.annotations.Wire;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.UUID;

public class EnergyBatterySerializerV1 implements Serializer<Integer, EnergyBatteryEditEntity> {
    private ComponentMapper<EnergyBatteryComponent> mEnergyBattery;
    private ComponentMapper<FaceComponent> mFace;
    @Wire(name = "systemsAdmin")
    private SystemsAdminCommun systemsAdminCommun;
    @Override
    public SerializedRawBytes toRawBytes(World world, SerializerController serializerController, Integer energyBatteryToSerialize) {
        ByteBuf buffer = Unpooled.buffer(getBytesSize(world, serializerController, energyBatteryToSerialize));
        EnergyBatteryComponent energyBatteryComponent = mEnergyBattery.get(energyBatteryToSerialize);
        FaceComponent faceComponent = mFace.get(energyBatteryToSerialize);

        ByteBufferHelper.writeUUID(buffer, systemsAdminCommun.getUuidEntityManager().getEntityUuid(energyBatteryToSerialize));
        buffer.writeLong(energyBatteryComponent.getStored());
        buffer.writeLong(energyBatteryComponent.getCapacity());

        ByteBufferHelper.encodeSerializedApplicationBytes(buffer, serializerController.getFaceSerializerController(), faceComponent);
        buffer.writeByte(faceComponent.getFlags());

        return new SerializedRawBytes(buffer.array());
    }

    @Override
    public EnergyBatteryEditEntity fromBytes(World world, SerializerController serializerController, SerializedRawBytes rawBytes) {
        ByteBuf buffer = Unpooled.wrappedBuffer(rawBytes.rawBytes());

        UUID energyBatteryUuid = ByteBufferHelper.readUUID(buffer);
        long stored = buffer.readLong();
        long capacity = buffer.readLong();

        byte face = ByteBufferHelper.decodeSerializedApplicationBytes(buffer, serializerController.getFaceSerializerController());
        return EnergyBatteryEditEntity.create(new UuidSupplierOrRandom(energyBatteryUuid), stored, capacity, face);
    }

    @Override
    public int getBytesSize(World world, SerializerController serializerController, Integer energyBatteryToSerialize) {
        FaceComponent faceComponent = mFace.get(energyBatteryToSerialize);
        int uuidLength = Long.BYTES * 2;
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
