package ch.realmtech.server.serialize.inventory;

import ch.realmtech.server.ctrl.ItemManager;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.serialize.SerializerManager;

import java.util.function.Function;

public interface InventorySerializerManager extends SerializerManager<InventoryComponent, Function<ItemManager, int[][]>> {
}
