package ch.realmtechCommuns.item;

import ch.realmtechCommuns.ecs.component.InventoryComponent;
import ch.realmtechCommuns.ecs.system.InventoryManager;
import com.artemis.World;

public interface ItemResultCraftPickEvent {
    void pick(final World world);

    static ItemResultCraftPickEvent removeAllOneItem(InventoryComponent inventoryToRemoveComponent) {
        return (world) -> world.getSystem(InventoryManager.class).removeAllOneItem(inventoryToRemoveComponent.inventory);
    }
}
