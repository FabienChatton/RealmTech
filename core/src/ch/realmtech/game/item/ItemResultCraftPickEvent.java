package ch.realmtech.game.item;

import ch.realmtech.game.ecs.component.InventoryComponent;
import ch.realmtech.game.ecs.system.InventoryManager;
import com.artemis.World;

public interface ItemResultCraftPickEvent {
    void pick(final World world);

    static ItemResultCraftPickEvent removeAllOneItem(InventoryComponent inventoryComponent) {
        return (world) -> world.getSystem(InventoryManager.class).removeAllOneItem(inventoryComponent.inventory);
    }
}
