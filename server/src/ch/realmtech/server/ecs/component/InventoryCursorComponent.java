package ch.realmtech.server.ecs.component;

import com.artemis.Component;
import com.artemis.annotations.EntityId;

public class InventoryCursorComponent extends Component {
    @EntityId
    private int inventoryId;

    public InventoryCursorComponent set(int inventoryId) {
        this.inventoryId = inventoryId;
        return this;
    }

    public int getInventoryId() {
        return inventoryId;
    }
}
