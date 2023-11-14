package ch.realmtech.server.ecs.component;

import com.artemis.Component;
import com.artemis.annotations.EntityId;

public class ChestComponent extends Component {
    @EntityId
    private int inventoryId;

    public ChestComponent set(int inventoryId) {
        this.inventoryId = inventoryId;
        return this;
    }
}
