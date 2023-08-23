package ch.realmtech.game.ecs.system;

import ch.realmtech.game.ecs.component.CraftingTableComponent;
import ch.realmtech.game.ecs.component.FurnaceComponent;
import ch.realmtech.game.ecs.component.InventoryComponent;
import ch.realmtech.game.ecs.component.ItemComponent;
import ch.realmtech.game.mod.RealmTechCoreMod;
import ch.realmtech.game.registery.ItemRegisterEntry;
import ch.realmtech.game.registery.RegistryEntry;
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
        InventoryComponent iconInventoryTimeToBurnComponent = mInventory.get(furnaceComponent.iconInventoryTimeToBurn);
        InventoryComponent iconInventoryCurentBurnTimeComponent = mInventory.get(furnaceComponent.iconInventoryCurentBurnTime);
        ItemComponent itemIconTimeToBurnComponent = mItem.get(iconInventoryTimeToBurnComponent.inventory[0][0]);
        if (furnaceComponent.timeToBurn > 0) {
            int pourDix = 10 - 10 * furnaceComponent.timeToBurn / furnaceComponent.itemBurn.getItemBehavior().getTimeToBurn();
            if (pourDix == 0) pourDix = 1;
            String format;
            if (pourDix >= 10) {
                format = "furnace-time-to-burn-10";
            } else {
                format = String.format("furnace-time-to-burn-0%d", pourDix);
            }
            ItemRegisterEntry iconTimeToBurn = RealmTechCoreMod.ITEMS.getEnfants().stream()
                    .filter(itemRegisterEntryRegistryEntry -> itemRegisterEntryRegistryEntry.getEntry().getTextureRegionName()
                            .equals(format))
                    .map(RegistryEntry::getEntry)
                    .findFirst()
                    .orElse(RealmTechCoreMod.NO_ITEM);
            if (itemIconTimeToBurnComponent == null || itemIconTimeToBurnComponent.itemRegisterEntry != iconTimeToBurn) {
                world.getSystem(InventoryManager.class).removeInventory(iconInventoryTimeToBurnComponent.inventory);
                int iconId = world.getSystem(ItemManager.class).newItemInventory(iconTimeToBurn);
                world.getSystem(InventoryManager.class).addItemToStack(iconInventoryTimeToBurnComponent.inventory[0], iconId);
            }
        } else {
            world.getSystem(InventoryManager.class).removeInventory(iconInventoryTimeToBurnComponent.inventory);
        }
    }
}
