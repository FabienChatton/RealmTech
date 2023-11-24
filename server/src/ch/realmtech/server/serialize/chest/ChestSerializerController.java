package ch.realmtech.server.serialize.chest;

import ch.realmtech.server.serialize.AbstractSerializerController;
import ch.realmtech.server.serialize.SerializerController;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class ChestSerializerController extends AbstractSerializerController<Integer, Integer> {

    public ChestSerializerController(SerializerController serializerController) {
        super(serializerController, "ChestInv\0".getBytes(StandardCharsets.UTF_8), new HashMap<>() {
            {
                put(1, new ChestSerializerV1());
            }
        }, 1);
    }
}
