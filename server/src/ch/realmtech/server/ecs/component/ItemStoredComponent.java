package ch.realmtech.server.ecs.component;

import com.artemis.Component;
import com.artemis.annotations.EntityId;

public class ItemStoredComponent extends Component {
    @EntityId
    public int inventoryId;
    public int inventorySlotId;
}
