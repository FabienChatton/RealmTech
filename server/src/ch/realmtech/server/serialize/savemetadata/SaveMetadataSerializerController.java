package ch.realmtech.server.serialize.savemetadata;

import ch.realmtech.server.serialize.AbstractSerializerController;
import ch.realmtech.server.serialize.SerializerController;

import java.util.HashMap;

public class SaveMetadataSerializerController extends AbstractSerializerController<Integer, Integer> {
    public SaveMetadataSerializerController(SerializerController serializerController) {
        super(serializerController, (byte) 16, new HashMap<>() {
            {
                put((byte) 1, new SaveMetadataSerializerV1());
                put((byte) 2, new SaveMetadataSerializerV2());
            }
        }, (byte) 2);
    }
}
