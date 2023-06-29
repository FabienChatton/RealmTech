package ch.realmtech.game.ecs.component;

import ch.realmtech.game.item.ItemResultCraftPickEvent;
import com.artemis.Component;

public class ItemResultCraftComponent extends Component {
    public ItemResultCraftPickEvent pickEvent = ItemResultCraftPickEvent.clearCraftingInventory();
}
