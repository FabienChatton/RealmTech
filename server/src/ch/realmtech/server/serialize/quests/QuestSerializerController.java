package ch.realmtech.server.serialize.quests;

import ch.realmtech.server.quests.QuestPlayerProperty;
import ch.realmtech.server.serialize.AbstractSerializerController;
import ch.realmtech.server.serialize.SerializerController;

import java.util.HashMap;
import java.util.List;

public class QuestSerializerController extends AbstractSerializerController<Integer, List<QuestPlayerProperty>> {
    public QuestSerializerController(SerializerController serializerController) {
        super(serializerController, (byte) 26, new HashMap<>() {
            {
                put((byte) 1, new QuestSerializerV1());
            }
        }, (byte) 1);
    }
}
