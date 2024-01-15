package ch.realmtech.server.ecs.component;

import com.artemis.Component;
import com.artemis.annotations.EntityId;

public class FurnaceComponent extends Component {
    @EntityId
    public int inventoryCarburant;
    public int remainingTickToBurn;
    public int tickProcess;
    public FurnaceComponent set(int inventoryCarburantId) {
        this.inventoryCarburant = inventoryCarburantId;
        return this;
    }
}
