package ch.realmtechCommuns.ecs.component;

import ch.realmtechCommuns.craft.CraftResult;
import ch.realmtechCommuns.registery.ItemRegisterEntry;
import com.artemis.Component;
import com.artemis.annotations.EntityId;

public class FurnaceComponent extends Component {
    @EntityId
    public int inventoryCarburant;
    @EntityId
    public int iconInventoryTimeToBurn;
    @EntityId
    public int iconInventoryCurentBurnTime;
    public int timeToBurn;
    public int curentBurnTime = 0;
    public CraftResult curentCraftResult = null;
    public ItemRegisterEntry itemBurn;

    public FurnaceComponent set(int inventoryCarburantId, int iconInventoryTimeToBurn, int iconInventoryCurentBurnTime) {
        this.inventoryCarburant = inventoryCarburantId;
        this.iconInventoryTimeToBurn = iconInventoryTimeToBurn;
        this.iconInventoryCurentBurnTime = iconInventoryCurentBurnTime;
        return this;
    }
}
