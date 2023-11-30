package ch.realmtech.server.serialize.chest;

import ch.realmtech.server.serialize.AbstractSerializerController;
import ch.realmtech.server.serialize.SerializerController;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.function.Consumer;

public class ChestSerializerController extends AbstractSerializerController<Integer, Consumer<Integer>> {

    public ChestSerializerController(SerializerController serializerController) {
        super(serializerController, (byte) 11, new HashMap<>() {
            {
                put((byte) 1, new ChestSerializerV1());
            }
        }, (byte) 1);
    }
}
