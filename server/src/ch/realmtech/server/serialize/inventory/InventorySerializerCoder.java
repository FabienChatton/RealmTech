package ch.realmtech.server.serialize.inventory;

import ch.realmtech.server.ctrl.ItemManager;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.serialize.SerializerCoder;

import java.util.function.Function;

public interface InventorySerializerCoder extends SerializerCoder<InventoryComponent, Function<ItemManager, int[][]>> {
}
