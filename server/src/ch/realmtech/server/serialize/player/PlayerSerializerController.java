package ch.realmtech.server.serialize.player;

import ch.realmtech.server.serialize.AbstractSerializerController;
import ch.realmtech.server.serialize.SerializerController;

import java.util.HashMap;
import java.util.function.Consumer;

public class PlayerSerializerController extends AbstractSerializerController<PlayerSerializerConfig, Consumer<Integer>> {
    public PlayerSerializerController(SerializerController serializerController) {
        super(serializerController, (byte) 21, new HashMap<>() {
            {
                put((byte) 1, new PlayerSerializerV1());
                put((byte) 2, new PlayerSerializerV2());
                put((byte) 3, new PlayerSerializerV3());
            }
        }, (byte) 3);
    }
}
