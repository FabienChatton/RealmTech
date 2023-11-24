package ch.realmtech.server.serialize;

import ch.realmtech.server.serialize.exception.IllegalMagicNumbers;
import ch.realmtech.server.serialize.types.SerializedApplicationBytes;
import ch.realmtech.server.serialize.types.SerializedRawBytes;
import ch.realmtech.server.serialize.types.SerializedVersionAndBytes;
import com.artemis.World;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;


public abstract class AbstractSerializerController<InputType, OutputType> implements SerializerCoder<InputType, OutputType> {
    public final static int MAGIC_NUMBER_LENGTH = 9;
    public final static int VERSION_LENGTH = Integer.BYTES;
    private final SerializerController serializerController;
    private final byte[] magicNumbers;
    private final Map<Integer, Serializer<InputType, OutputType>> serializerMap;
    private final int latestVersion;

    public AbstractSerializerController(SerializerController serializerController, byte[] magicNumbers, Map<Integer, Serializer<InputType, OutputType>> serializerMap, int latestVersion) {
        this.serializerController = serializerController;
        if (magicNumbers.length != 9) throw new IllegalMagicNumbers("Magic number must be of length 9");
        this.magicNumbers = magicNumbers;
        this.serializerMap = serializerMap;
        this.latestVersion = latestVersion;
    }

    @Override
    public SerializedApplicationBytes encode(World world, InputType toSerialize) {
        return encodeApplicationBytes(world, toSerialize);
    }

    @Override
    public OutputType decode(World world, SerializedApplicationBytes applicationBytes) {
        return decodeApplicationBytes(world, applicationBytes);
    }

    byte[] getMagicNumbers() {
        return magicNumbers;
    }

    Serializer<InputType, OutputType> getSerializer(int version) {
        return serializerMap.get(version);
    }

    Serializer<InputType, OutputType> getSerializer() {
        return getSerializer(latestVersion);
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

    private boolean testMagicNumbers(SerializedApplicationBytes applicationBytes, AbstractSerializerController<?, ?> serializerManager) {
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
