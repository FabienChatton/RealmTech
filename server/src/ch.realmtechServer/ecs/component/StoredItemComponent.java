package ch.realmtechServer.ecs.component;

import com.artemis.Component;
import com.artemis.annotations.EntityId;

public class StoredItemComponent extends Component {
    @EntityId
    public int inventoryId;
    public int inventorySlotId;
}
