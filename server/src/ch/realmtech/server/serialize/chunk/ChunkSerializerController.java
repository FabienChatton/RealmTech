package ch.realmtech.server.serialize.chunk;

import ch.realmtech.server.ecs.component.InfChunkComponent;
import ch.realmtech.server.serialize.AbstractSerializerController;
import ch.realmtech.server.serialize.SerializerController;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class ChunkSerializerController extends AbstractSerializerController<InfChunkComponent, Integer> {
    public ChunkSerializerController(SerializerController serializerController) {
        super(serializerController, "chunkSav\0".getBytes(StandardCharsets.UTF_8), new HashMap<>() {
            {
                put(9, new ChunkSerializerV9());
            }
        }, 9);
    }
}
