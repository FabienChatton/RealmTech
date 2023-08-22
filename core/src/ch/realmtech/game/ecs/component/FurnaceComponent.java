package ch.realmtech.game.ecs.component;

import ch.realmtech.game.craft.CraftResult;
import com.artemis.Component;
import com.artemis.annotations.EntityId;

public class FurnaceComponent extends Component {
    @EntityId
    public int inventoryCarburant;
    public int timeToBurn;
    public int curentBurnTime = 0;
    public CraftResult curentCraftResult = null;

    public FurnaceComponent set(int inventoryCarburantId) {
        this.inventoryCarburant = inventoryCarburantId;
        return this;
    }
}
