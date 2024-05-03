package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.game.ecs.component.FurnaceExtraInfoComponent;
import ch.realmtech.core.game.ecs.plugin.SystemsAdminClient;
import ch.realmtech.server.craft.CraftResult;
import ch.realmtech.server.ecs.component.CraftingTableComponent;
import ch.realmtech.server.ecs.component.FurnaceComponent;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.component.ItemComponent;
import ch.realmtech.server.ecs.plugin.SystemServerTickSlave;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;

import java.util.Optional;

@SystemServerTickSlave
@All({CraftingTableComponent.class, FurnaceComponent.class, FurnaceExtraInfoComponent.class})
public class FurnaceSimulationSystem extends IteratingSystem {
    @Wire
    private SystemsAdminClient systemsAdminClient;
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
        InventoryComponent craftingResultInventoryComponent = mInventory.get(craftingTableComponent.craftingResultInventory);

        if (furnaceComponent.remainingTickToBurn > 0) {
            furnaceComponent.remainingTickToBurn--;
        }

        Optional<CraftResult> craftResultOpt = systemsAdminClient.getCraftingManager().getNewCraftResult(craftingTableComponent);
        ItemComponent itemWitness = mItem.get(craftingResultInventoryComponent.inventory[0][0]);

        if (craftResultOpt.isEmpty()) {
            furnaceComponent.tickProcess = 0;
            return;
        }

        CraftResult craftResult = craftResultOpt.get();
        if (itemWitness != null) {
            if (itemWitness.itemRegisterEntry != craftResult.getItemResult()) {
                furnaceComponent.tickProcess = 0;
                return;
            }
        }

        if (furnaceComponent.tickProcess < furnaceExtraInfoComponent.lastTickProcessFull) {
            furnaceComponent.tickProcess++;
        } else {
            furnaceComponent.tickProcess = 0;
        }
    }
}
