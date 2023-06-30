package ch.realmtech.game.item;

import ch.realmtech.game.ecs.component.InventoryComponent;
import ch.realmtech.game.ecs.system.InventoryManager;
import com.artemis.World;

public interface ItemResultCraftPickEvent {
    void pick(final World world, final InventoryComponent inventoryComponent);

    static ItemResultCraftPickEvent clearCraftingInventory() {
        return (world, inventoryComponent) -> world.getSystem(InventoryManager.class).clearInventory(inventoryComponent.inventory);
    }
}
