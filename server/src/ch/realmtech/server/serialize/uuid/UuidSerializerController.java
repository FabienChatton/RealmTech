package ch.realmtech.server.serialize.uuid;

import ch.realmtech.server.serialize.AbstractSerializerController;
import ch.realmtech.server.serialize.SerializerController;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.UUID;

public class UuidSerializerController extends AbstractSerializerController<UUID, UUID> {
    public UuidSerializerController(SerializerController serializerController) {
        super(serializerController, "uuidSave\0".getBytes(StandardCharsets.UTF_8), new HashMap<>() {
            {
                put(1, new UuidSerializerV1());
            }
        }, 1);
    }
}
