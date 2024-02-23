package ch.realmtech.server.serialize.craftingtable;

import ch.realmtech.server.level.cell.CraftingTableEditEntity;
import ch.realmtech.server.serialize.AbstractSerializerController;
import ch.realmtech.server.serialize.SerializerController;

import java.util.HashMap;

public class CraftingTableController extends AbstractSerializerController<Integer, CraftingTableEditEntity> {

    public CraftingTableController(SerializerController serializerController) {
        super(serializerController, (byte) 17, new HashMap<>() {
            {
                put((byte) 1, new CraftingTableSerializerV1());
                put((byte) 2, new CraftingTableSerializerV2());
            }
        }, (byte) 2);
    }
}
