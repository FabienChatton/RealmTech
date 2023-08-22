package ch.realmtech.game.ecs.component;

import ch.realmtech.game.item.ItemResultCraftPickEvent;
import com.artemis.Component;

public class ItemResultCraftComponent extends Component {
    public ItemResultCraftPickEvent pickEvent;

    public ItemResultCraftComponent set(ItemResultCraftPickEvent pickEvent) {
        this.pickEvent = pickEvent;
        return this;
    }
}
