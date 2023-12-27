package ch.realmtech.server.serialize.player;

import ch.realmtech.server.serialize.AbstractSerializerController;
import ch.realmtech.server.serialize.SerializerController;

import java.util.HashMap;
import java.util.function.Consumer;

public class PlayerSerializerController extends AbstractSerializerController<Integer, Consumer<Integer>> {
    public PlayerSerializerController(SerializerController serializerController) {
        super(serializerController, (byte) 21, new HashMap<>() {
            {
                put((byte) 1, new PlayerSerializerV1());
            }
        }, (byte) 1);
    }
}
