package ch.realmtech.server.serialize.furnace;

import ch.realmtech.server.serialize.AbstractSerializerController;
import ch.realmtech.server.serialize.SerializerController;

import java.util.HashMap;
import java.util.function.Consumer;

public class FurnaceSerializerController extends AbstractSerializerController<Integer, Consumer<Integer>> {
    public FurnaceSerializerController(SerializerController serializerController) {
        super(serializerController, (byte) 18, new HashMap<>() {
            {
                put((byte) 1, new FurnaceSerializerV1());
            }
        }, (byte) 1);
    }
}
