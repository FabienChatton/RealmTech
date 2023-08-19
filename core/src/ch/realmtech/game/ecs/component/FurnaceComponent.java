package ch.realmtech.game.ecs.component;

import com.artemis.Component;
import com.artemis.annotations.EntityId;

public class FurnaceComponent extends Component {
    @EntityId
    public int inventoryItemToSmelt;
    @EntityId
    public int inventoryCarburant;
    @EntityId
    public int inventoryResult;

    public FurnaceComponent set(int inventoryItemToSmeltId, int inventoryCarburantId, int inventoryResultId) {
        this.inventoryItemToSmelt = inventoryItemToSmeltId;
        this.inventoryCarburant = inventoryCarburantId;
        this.inventoryResult = inventoryResultId;
        return this;
    }
}
