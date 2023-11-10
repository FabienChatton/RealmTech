package ch.realmtechServer.item;

import ch.realmtechServer.ecs.component.InventoryComponent;
import ch.realmtechServer.ecs.system.InventoryManager;
import com.artemis.World;

public interface ItemResultCraftPickEvent {
    void pick(final World world);

    static ItemResultCraftPickEvent removeAllOneItem(InventoryComponent inventoryToRemoveComponent) {
        return (world) -> world.getSystem(InventoryManager.class).deleteAllOneItem(inventoryToRemoveComponent.inventory);
    }
}
