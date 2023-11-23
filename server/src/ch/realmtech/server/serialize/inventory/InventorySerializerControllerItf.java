package ch.realmtech.server.serialize.inventory;

import ch.realmtech.server.ctrl.ItemManager;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.serialize.SerializerControllerItf;

import java.util.function.Function;

public interface InventorySerializerControllerItf extends SerializerControllerItf<InventoryComponent, Function<ItemManager, int[][]>> {
}
