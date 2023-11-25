package ch.realmtech.server.serialize.uuid;

import ch.realmtech.server.serialize.AbstractSerializerController;
import ch.realmtech.server.serialize.SerializerController;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.UUID;

public class UuidSerializerController extends AbstractSerializerController<UUID, UUID> {
    public UuidSerializerController(SerializerController serializerController) {
        super(serializerController, (byte) 14, new HashMap<>() {
            {
                put((byte) 1, new UuidSerializerV1());
            }
        }, (byte) 1);
    }
}
