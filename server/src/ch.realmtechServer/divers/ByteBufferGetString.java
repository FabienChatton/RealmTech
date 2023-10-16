package ch.realmtechServer.divers;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ByteBufferGetString {
    public static String getString(ByteBuffer byteBuffer) {
        List<Character> bytesList = new ArrayList<>();
        char c;
        do {
            c = byteBuffer.getChar();
            bytesList.add(c);
        } while (c != '\0');
        Character[] characters = bytesList.toArray(new Character[0]);
        return Arrays.toString(characters);
    }
}
