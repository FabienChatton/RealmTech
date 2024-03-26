package ch.realmtech.server.item;

import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import com.artemis.ComponentMapper;
import com.artemis.World;

public interface ItemResultCraftPickEvent {
    /**
     * What happen when a item is pick, usualy, somme items in oder inventory are removed.
     * @param world The world where pick event will be executed
     * @return All mutated inventory.
     */
    int[] pick(final World world);

    static ItemResultCraftPickEvent removeAllOneItem(int inventoryToRemove) {
        return (world) -> {
            ComponentMapper<InventoryComponent> mInventory = world.getMapper(InventoryComponent.class);
            InventoryComponent inventoryToRemoveComponent = mInventory.get(inventoryToRemove);
            ((SystemsAdminCommun) world.getRegistered("systemsAdmin")).getInventoryManager().deleteAllOneItem(inventoryToRemoveComponent.inventory);
            return new int[]{inventoryToRemove};
        };
    }
}
