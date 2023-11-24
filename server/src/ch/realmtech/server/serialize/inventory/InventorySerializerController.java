package ch.realmtech.server.serialize.inventory;

import ch.realmtech.server.ctrl.ItemManager;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.serialize.AbstractSerializerController;
import ch.realmtech.server.serialize.SerializerController;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.function.Function;

public class InventorySerializerController extends AbstractSerializerController<InventoryComponent, Function<ItemManager, InventoryArgs>> {

    public InventorySerializerController(SerializerController serializerController) {
        super(serializerController, "invBytes\0".getBytes(StandardCharsets.UTF_8), new HashMap<>(){
            {
                put(3, new InventorySerializerV3());
            }
        }, 3);
    }
}
