package ch.realmtechServer.divers;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public final class ByteBufferGetString {
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
}
