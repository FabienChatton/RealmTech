package ch.realmtech.server.serialize;

import ch.realmtech.server.serialize.exception.IllegalMagicNumbers;
import ch.realmtech.server.serialize.types.SerializedApplicationBytes;
import ch.realmtech.server.serialize.types.SerializedRawBytes;
import ch.realmtech.server.serialize.types.SerializedVersionAndBytes;
import com.artemis.World;

import java.nio.ByteBuffer;
import java.util.Arrays;


public abstract class AbstractSerializerController<InputType, OutputType> implements SerializerCoder<InputType, OutputType>, SerializerManager<InputType, OutputType> {
    private final static int MAGIC_NUMBER_LENGTH = 9;
    private final SerializerController serializerController;
    private final byte[] magicNumbers;

    public AbstractSerializerController(SerializerController serializerController, byte[] magicNumbers) {
        this.serializerController = serializerController;
        if (magicNumbers.length != 9) throw new IllegalMagicNumbers("Magic number must be of length 9");
        this.magicNumbers = magicNumbers;
    }

    @Override
    public SerializedApplicationBytes encode(World world, InputType toSerialize) {
        return encodeApplicationBytes(world, toSerialize);
    }

    @Override
    public OutputType decode(World world, SerializedApplicationBytes applicationBytes) {
        return decodeApplicationBytes(world, applicationBytes);
    }

    @Override
    public byte[] getMagicNumbers() {
        return magicNumbers;
    }

    private OutputType decodeVersionAndBytes(World world, SerializedVersionAndBytes serializedVersionAndBytes) {
        int version = getVersion(serializedVersionAndBytes);
        Serializer<?, OutputType> serializer = getSerializer(version);
        return serializer.fromBytes(world, serializerController, shrinkVersion(serializedVersionAndBytes));
    }

    private OutputType decodeApplicationBytes(World world, SerializedApplicationBytes applicationBytes) {
        if (!testMagicNumbers(applicationBytes, this)) throw new IllegalMagicNumbers();
        return decodeVersionAndBytes(world, shrinkMagicNumbers(applicationBytes));
    }

    private byte[] getMagicNumbers(SerializedApplicationBytes applicationBytes) {
        byte[] magicNumbers = new byte[MAGIC_NUMBER_LENGTH];
        System.arraycopy(applicationBytes.applicationBytes(), 0, magicNumbers, 0, magicNumbers.length);
        return magicNumbers;
    }

    private boolean testMagicNumbers(SerializedApplicationBytes applicationBytes, SerializerManager<?, ?> serializerManager) {
        return Arrays.equals(getMagicNumbers(applicationBytes), serializerManager.getMagicNumbers());
    }

    private SerializedVersionAndBytes shrinkMagicNumbers(SerializedApplicationBytes serializedApplicationBytes) {
        byte[] shrinkBytes = new byte[serializedApplicationBytes.applicationBytes().length - MAGIC_NUMBER_LENGTH];
        System.arraycopy(serializedApplicationBytes.applicationBytes(), MAGIC_NUMBER_LENGTH, shrinkBytes, 0, shrinkBytes.length);
        return new SerializedVersionAndBytes(shrinkBytes);
    }

    private SerializedRawBytes shrinkVersion(SerializedVersionAndBytes versionAndBytes) {
        byte[] shrinkBytes = new byte[versionAndBytes.versionAndBytes().length - Integer.BYTES];
        System.arraycopy(versionAndBytes.versionAndBytes(), Integer.BYTES, shrinkBytes, 0, shrinkBytes.length);
        return new SerializedRawBytes(shrinkBytes);
    }

    private int getVersion(SerializedVersionAndBytes versionAndBytes) {
        byte[] tempVersionBytes = new byte[Integer.BYTES];
        System.arraycopy(versionAndBytes.versionAndBytes(), 0, tempVersionBytes, 0, tempVersionBytes.length);
        ByteBuffer byteBuffer = ByteBuffer.wrap(tempVersionBytes);
        return byteBuffer.getInt();
    }

    private SerializedVersionAndBytes encodeVersionAndBytes(World world, InputType toSerialize) {
        SerializedRawBytes rawBytes = getSerializer().toRawBytes(world, serializerController, toSerialize);
        ByteBuffer versionAndBytesBuffer = ByteBuffer.allocate(rawBytes.rawBytes().length + Integer.BYTES);
        versionAndBytesBuffer.putInt(getSerializer().getVersion());
        versionAndBytesBuffer.put(rawBytes.rawBytes());
        return new SerializedVersionAndBytes(versionAndBytesBuffer.array());
    }

    private SerializedApplicationBytes encodeApplicationBytes(World world, InputType toSerialize) {
        SerializedVersionAndBytes versionAndBytes = encodeVersionAndBytes(world, toSerialize);
        ByteBuffer applicationBytesBuffer = ByteBuffer.allocate(versionAndBytes.versionAndBytes().length + MAGIC_NUMBER_LENGTH);
        applicationBytesBuffer.put(getMagicNumbers());
        applicationBytesBuffer.put(versionAndBytes.versionAndBytes());
        return new SerializedApplicationBytes(applicationBytesBuffer.array());
    }
}
