package ch.realmtech.server.serialize.savemetadata;

import ch.realmtech.server.ecs.component.SaveMetadataComponent;
import ch.realmtech.server.serialize.AbstractSerializerController;
import ch.realmtech.server.serialize.SerializerController;

import java.util.HashMap;

public class SaveMetadataSerializerController extends AbstractSerializerController<SaveMetadataComponent, Integer> {
    public SaveMetadataSerializerController(SerializerController serializerController) {
        super(serializerController, (byte) 16, new HashMap<>() {
            {
                put((byte) 1, new SaveMetadataSerializerV1());
            }
        }, (byte) 1);
    }
}
