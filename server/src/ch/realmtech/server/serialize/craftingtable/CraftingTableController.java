package ch.realmtech.server.serialize.craftingtable;

import ch.realmtech.server.serialize.AbstractSerializerController;
import ch.realmtech.server.serialize.SerializerController;

import java.util.HashMap;
import java.util.function.Consumer;

public class CraftingTableController extends AbstractSerializerController<Integer, Consumer<Integer>> {

    public CraftingTableController(SerializerController serializerController) {
        super(serializerController, (byte) 17, new HashMap<>() {
            {
                put((byte) 1, new CraftingTableSerializerV1());
            }
        }, (byte) 1);
    }
}
