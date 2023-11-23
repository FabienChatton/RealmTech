package ch.realmtech.server.serialize;

import ch.realmtech.server.serialize.exception.IllegalMagicNumbers;
import ch.realmtech.server.serialize.inventory.InventorySerializerCoder;
import ch.realmtech.server.serialize.inventory.InventorySerializerControllerItf;
import ch.realmtech.server.serialize.types.SerializedApplicationBytes;
import ch.realmtech.server.serialize.types.SerializedRawBytes;
import ch.realmtech.server.serialize.types.SerializedVersionAndBytes;
import com.artemis.World;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class SerializerController {
    public final static int MAGIC_NUMBER_LENGTH = 9;
    private final InventorySerializerControllerItf inventorySerializerManager = new InventorySerializerController(this);

    byte[] getMagicNumbers(SerializedApplicationBytes applicationBytes) {
        byte[] magicNumbers = new byte[MAGIC_NUMBER_LENGTH];
        System.arraycopy(applicationBytes.applicationBytes(), 0, magicNumbers, 0, magicNumbers.length);
        return magicNumbers;
    }

    SerializedVersionAndBytes shrinkMagicNumbers(SerializedApplicationBytes serializedApplicationBytes) {
        byte[] shrinkBytes = new byte[serializedApplicationBytes.applicationBytes().length - MAGIC_NUMBER_LENGTH];
        System.arraycopy(serializedApplicationBytes.applicationBytes(), MAGIC_NUMBER_LENGTH, shrinkBytes, 0, shrinkBytes.length);
        return new SerializedVersionAndBytes(shrinkBytes);
    }

    SerializedRawBytes shrinkVersion(SerializedVersionAndBytes versionAndBytes) {
        byte[] shrinkBytes = new byte[versionAndBytes.versionAndBytes().length - Integer.BYTES];
        System.arraycopy(versionAndBytes.versionAndBytes(), Integer.BYTES, shrinkBytes, 0, shrinkBytes.length);
        return new SerializedRawBytes(shrinkBytes);
    }

    int getVersion(SerializedVersionAndBytes versionAndBytes) {
        byte[] tempVersionBytes = new byte[Integer.BYTES];
        System.arraycopy(versionAndBytes.versionAndBytes(), 0, tempVersionBytes, 0, tempVersionBytes.length);
        ByteBuffer byteBuffer = ByteBuffer.wrap(tempVersionBytes);
        return byteBuffer.getInt();
    }

    <InputType> SerializedVersionAndBytes encodeVersionAndBytes(World world, Serializer<InputType, ?> serializer, InputType toSerialize) {
        SerializedRawBytes rawBytes = serializer.toRawBytes(world, this, toSerialize);
        ByteBuffer versionAndBytesBuffer = ByteBuffer.allocate(rawBytes.rawBytes().length + Integer.BYTES);
        versionAndBytesBuffer.putInt(serializer.getVersion());
        versionAndBytesBuffer.put(rawBytes.rawBytes());
        return new SerializedVersionAndBytes(versionAndBytesBuffer.array());
    }

    <InputType> SerializedApplicationBytes encodeApplicationBytes(World world, SerializerManager<InputType, ?> serializerManager, InputType toSerialize) {
        SerializedVersionAndBytes versionAndBytes = encodeVersionAndBytes(world, serializerManager.getSerializer(), toSerialize);
        ByteBuffer applicationBytesBuffer = ByteBuffer.allocate(versionAndBytes.versionAndBytes().length + SerializerController.MAGIC_NUMBER_LENGTH);
        applicationBytesBuffer.put(serializerManager.getMagicNumbers());
        applicationBytesBuffer.put(versionAndBytes.versionAndBytes());
        return new SerializedApplicationBytes(applicationBytesBuffer.array());
    }

    <OutputType> OutputType decodeVersionAndBytes(World world, SerializerManager<?, OutputType> serializerManager, SerializedVersionAndBytes serializedVersionAndBytes) {
        int version = getVersion(serializedVersionAndBytes);
        Serializer<?, OutputType> serializer = serializerManager.getSerializer(version);
        return serializer.fromBytes(world, this, shrinkVersion(serializedVersionAndBytes));
    }

    <OutputType> OutputType decodeApplicationBytes(World world, SerializerControllerItf<?, OutputType> serializerManager, SerializedApplicationBytes applicationBytes) {
        if (!testMagicNumbers(applicationBytes, serializerManager)) throw new IllegalMagicNumbers();
        return decodeVersionAndBytes(world, serializerManager, shrinkMagicNumbers(applicationBytes));
    }

    boolean testMagicNumbers(SerializedApplicationBytes applicationBytes, SerializerManager<?, ?> serializerManager) {
        return Arrays.equals(getMagicNumbers(applicationBytes), serializerManager.getMagicNumbers());
    }

    public InventorySerializerCoder getInventorySerializerManager() {
        return inventorySerializerManager;
    }
}
