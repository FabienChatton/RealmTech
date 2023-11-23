package ch.realmtech.server.serialize.inventory;

import ch.realmtech.server.ctrl.ItemManager;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.serialize.Serializer;

import java.util.function.Function;

public interface InventorySerializer extends Serializer<InventoryComponent, Function<ItemManager, int[][]>> {

}
