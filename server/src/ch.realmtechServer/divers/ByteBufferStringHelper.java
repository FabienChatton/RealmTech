package ch.realmtechServer.divers;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class ByteBufferStringHelper {

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
}
