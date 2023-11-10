package ch.realmtechServer.divers;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class ByteBufferHelper {

    public static String getString(ByteBuf byteBuf) {
        List<Byte> bytesList = new ArrayList<>();
        byte c;
        do {
            c = byteBuf.readByte();
            if (c != '\0') {
                bytesList.add(c);
            }
        } while (c != '\0');
        Byte[] characters = bytesList.toArray(new Byte[0]);
        byte[] chars = new byte[characters.length];
        for (int i = 0; i < characters.length; i++) {
            chars[i] = characters[i];
        }
        return new String(chars);
    }
    public static String getString(ByteBuffer byteBuffer) {
        List<Byte> bytesList = new ArrayList<>();
        byte c;
        do {
            c = byteBuffer.get();
            if (c != '\0') {
                bytesList.add(c);
            }
        } while (c != '\0');
        Byte[] characters = bytesList.toArray(new Byte[0]);
        byte[] chars = new byte[characters.length];
        for (int i = 0; i < characters.length; i++) {
            chars[i] = characters[i];
        }
        return new String(chars);
    }

    public static ByteBuf writeString(ByteBuf byteBuf, String string) {
        byteBuf.writeBytes((string + "\0").getBytes(StandardCharsets.US_ASCII));
        return byteBuf;
    }

    public static ByteBuffer writeString(ByteBuffer byteBuffer, String string) {
        byteBuffer.put((string + "\0").getBytes(StandardCharsets.US_ASCII));
        return byteBuffer;
    }

    /**
     * Read a uuid from the byte buf. Increment the index of the byte buf by 16.
     * @param byteBuf The byte buf input.
     * @return A valid uuid.
     * @throws IllegalArgumentException If this uuid is not valide
     */
    public static UUID readUUID(ByteBuf byteBuf) {
        long msb = byteBuf.readLong();
        long lsb = byteBuf.readLong();
        UUID uuid = new UUID(msb, lsb);

        // test si valide
        return UUID.fromString(uuid.toString());
    }

    /**
     * Write a uuid to the byte buf. Increment the index of the byte buf by 16.
     * @param byteBuf The byte buf to write into.
     * @param uuid The uuid to write in the buffer
     * @return The input buffer
     */
    public static ByteBuf writeUUID(ByteBuf byteBuf, UUID uuid) {
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        byteBuf.writeLong(msb);
        byteBuf.writeLong(lsb);
        return byteBuf;
    }
}
