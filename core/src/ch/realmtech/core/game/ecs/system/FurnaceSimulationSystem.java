package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.game.ecs.component.FurnaceExtraInfoComponent;
import ch.realmtech.server.ecs.component.CraftingTableComponent;
import ch.realmtech.server.ecs.component.FurnaceComponent;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.component.ItemComponent;
import ch.realmtech.server.ecs.plugin.SystemServerTickSlave;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;

@SystemServerTickSlave
@All({CraftingTableComponent.class, FurnaceComponent.class, FurnaceExtraInfoComponent.class})
public class FurnaceSimulationSystem extends IteratingSystem {
    private ComponentMapper<FurnaceComponent> mFurnace;
    private ComponentMapper<FurnaceExtraInfoComponent> mFurnaceExtraInfo;
    private ComponentMapper<CraftingTableComponent> mCraftingTable;
    private ComponentMapper<InventoryComponent> mInventory;
    private ComponentMapper<ItemComponent> mItem;
    @Override
    protected void process(int entityId) {
        FurnaceComponent furnaceComponent = mFurnace.get(entityId);
        FurnaceExtraInfoComponent furnaceExtraInfoComponent = mFurnaceExtraInfo.get(entityId);
        CraftingTableComponent craftingTableComponent = mCraftingTable.get(entityId);

        if (furnaceComponent.remainingTickToBurn > 0) {
            furnaceComponent.remainingTickToBurn--;
        }

        if (furnaceComponent.remainingTickToBurn > 0 && mItem.has(mInventory.get(craftingTableComponent.craftingInventory).inventory[0][0])) {
            if (furnaceComponent.tickProcess < furnaceExtraInfoComponent.lastTickProcessFull) {
                furnaceComponent.tickProcess++;
            }
        } else {
            furnaceComponent.tickProcess = 0;
        }
    }
}
