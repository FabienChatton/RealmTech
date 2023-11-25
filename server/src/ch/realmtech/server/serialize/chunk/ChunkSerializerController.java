package ch.realmtech.server.serialize.chunk;

import ch.realmtech.server.ecs.component.InfChunkComponent;
import ch.realmtech.server.serialize.AbstractSerializerController;
import ch.realmtech.server.serialize.SerializerController;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class ChunkSerializerController extends AbstractSerializerController<InfChunkComponent, Integer> {
    public ChunkSerializerController(SerializerController serializerController) {
        super(serializerController, (byte) 12, new HashMap<>() {
            {
                put((byte) 9, new ChunkSerializerV9());
            }
        }, (byte) 9);
    }
}
