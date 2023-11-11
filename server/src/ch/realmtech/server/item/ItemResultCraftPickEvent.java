package ch.realmtech.server.item;

import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.system.InventoryManager;
import com.artemis.World;

public interface ItemResultCraftPickEvent {
    void pick(final World world);

    static ItemResultCraftPickEvent removeAllOneItem(InventoryComponent inventoryToRemoveComponent) {
        return (world) -> world.getSystem(InventoryManager.class).deleteAllOneItem(inventoryToRemoveComponent.inventory);
    }
}
