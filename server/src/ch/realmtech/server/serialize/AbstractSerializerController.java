package ch.realmtech.server.serialize;

import ch.realmtech.server.serialize.exception.IllegalMagicNumbers;
import ch.realmtech.server.serialize.types.SerializedApplicationBytes;
import ch.realmtech.server.serialize.types.SerializedRawBytes;
import ch.realmtech.server.serialize.types.SerializedVersionAndBytes;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Map;


public abstract class AbstractSerializerController<InputType, OutputType> implements SerializerCoder<InputType, OutputType> {
    public final static int MAGIC_NUMBER_LENGTH = 1;
    public final static int VERSION_LENGTH = 1;
    public final static int MAX_BUFFER_LENGTH = Short.MAX_VALUE;
    private final SerializerController serializerController;
    private final byte magicNumber;
    private final Map<Byte, Serializer<InputType, OutputType>> serializerMap;
    private final byte latestVersion;

    public AbstractSerializerController(SerializerController serializerController, byte magicNumber, Map<Byte, Serializer<InputType, OutputType>> serializerMap, byte latestVersion) {
        this.serializerController = serializerController;
        this.magicNumber = magicNumber;
        this.serializerMap = serializerMap;
        this.latestVersion = latestVersion;
    }

    @Override
    public SerializedApplicationBytes encode(InputType toSerialize) {
        return encodeApplicationBytes(toSerialize);
    }

    @Override
    public OutputType decode(SerializedApplicationBytes applicationBytes) {
        return decodeApplicationBytes(applicationBytes);
    }

    byte getMagicNumber() {
        return magicNumber;
    }


    public Map<Byte, Serializer<InputType, OutputType>> getSerializers() {
        return Collections.unmodifiableMap(serializerMap);
    }

    Serializer<InputType, OutputType> getSerializer(byte version) {
        return getSerializers().get(version);
    }

    Serializer<InputType, OutputType> getSerializer() {
        return getSerializer(latestVersion);
    }

    private OutputType decodeVersionAndBytes(SerializedVersionAndBytes serializedVersionAndBytes) {
        byte version = getVersion(serializedVersionAndBytes);
        Serializer<?, OutputType> serializer = getSerializer(version);
        return serializer.fromBytes(serializerController.getGetWorld(), serializerController, shrinkVersion(serializedVersionAndBytes));
    }

    private OutputType decodeApplicationBytes(SerializedApplicationBytes applicationBytes) {
        if (!testMagicNumbers(applicationBytes, this)) {
            AbstractSerializerController<?, Object> expectedSerializer = serializerController.getSerializerControllerByMagic(getMagicNumber(applicationBytes));
            throw new IllegalMagicNumbers("Bytes magic number: (" + expectedSerializer.getClass().getSimpleName() + "," + expectedSerializer.getMagicNumber() + ")"
                    + " Actual: (" + getClass().getSimpleName() + "," + getMagicNumber() + ")");
        }
        return decodeVersionAndBytes(shrinkMagicNumbers(applicationBytes));
    }

    public static byte getMagicNumber(SerializedApplicationBytes applicationBytes) {
        return applicationBytes.applicationBytes()[0];
    }

    private boolean testMagicNumbers(SerializedApplicationBytes applicationBytes, AbstractSerializerController<?, ?> serializerManager) {
        return getMagicNumber(applicationBytes) == serializerManager.getMagicNumber();
    }

    private SerializedVersionAndBytes shrinkMagicNumbers(SerializedApplicationBytes serializedApplicationBytes) {
        byte[] shrinkBytes = new byte[serializedApplicationBytes.applicationBytes().length - MAGIC_NUMBER_LENGTH];
        System.arraycopy(serializedApplicationBytes.applicationBytes(), MAGIC_NUMBER_LENGTH, shrinkBytes, 0, shrinkBytes.length);
        return new SerializedVersionAndBytes(shrinkBytes);
    }

    private SerializedRawBytes shrinkVersion(SerializedVersionAndBytes versionAndBytes) {
        byte[] shrinkBytes = new byte[versionAndBytes.versionAndBytes().length - VERSION_LENGTH];
        System.arraycopy(versionAndBytes.versionAndBytes(), VERSION_LENGTH, shrinkBytes, 0, shrinkBytes.length);
        return new SerializedRawBytes(shrinkBytes);
    }

    private byte getVersion(SerializedVersionAndBytes versionAndBytes) {
        return versionAndBytes.versionAndBytes()[0];
    }

    private SerializedVersionAndBytes encodeVersionAndBytes(InputType toSerialize) {
        SerializedRawBytes rawBytes = getSerializer().toRawBytes(serializerController.getGetWorld(), serializerController, toSerialize);
        ByteBuffer versionAndBytesBuffer = ByteBuffer.allocate(rawBytes.rawBytes().length + VERSION_LENGTH);
        versionAndBytesBuffer.put(getSerializer().getVersion());
        versionAndBytesBuffer.put(rawBytes.rawBytes());
        return new SerializedVersionAndBytes(versionAndBytesBuffer.array());
    }

    private SerializedApplicationBytes encodeApplicationBytes(InputType toSerialize) {
        SerializedVersionAndBytes versionAndBytes = encodeVersionAndBytes(toSerialize);
        ByteBuffer applicationBytesBuffer = ByteBuffer.allocate(versionAndBytes.versionAndBytes().length + MAGIC_NUMBER_LENGTH);
        applicationBytesBuffer.put(getMagicNumber());
        applicationBytesBuffer.put(versionAndBytes.versionAndBytes());
        return new SerializedApplicationBytes(applicationBytesBuffer.array());
    }
}
