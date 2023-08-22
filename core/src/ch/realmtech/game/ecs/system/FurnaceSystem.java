package ch.realmtech.game.ecs.system;

import ch.realmtech.game.ecs.component.CraftingTableComponent;
import ch.realmtech.game.ecs.component.FurnaceComponent;
import ch.realmtech.game.ecs.component.InventoryComponent;
import ch.realmtech.game.ecs.component.ItemComponent;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;

@All({FurnaceComponent.class, CraftingTableComponent.class})
public class FurnaceSystem extends IteratingSystem {
    private ComponentMapper<FurnaceComponent> mFurnace;
    private ComponentMapper<ItemComponent> mItem;
    private ComponentMapper<InventoryComponent> mInventory;

    @Override
    protected void process(int entityId) {
        FurnaceComponent furnaceComponent = mFurnace.get(entityId);
        if (furnaceComponent.timeToBurn == 0) {
            InventoryComponent inventoryCarburant = mInventory.get(furnaceComponent.inventoryCarburant);
            int[] stack = inventoryCarburant.inventory[0];
            int topItem = InventoryManager.getTopItem(stack);
            if (mItem.has(topItem)) {
                ItemComponent burnitemComponent = mItem.get(topItem);
                int timeToBurn = burnitemComponent.itemRegisterEntry.getItemBehavior().getTimeToBurn();
                if (timeToBurn > 0) {
                    furnaceComponent.timeToBurn = timeToBurn;
                    world.getSystem(InventoryManager.class).removeOneItem(stack);
                }
            }
        } else {
            furnaceComponent.timeToBurn--;
        }
    }
}
