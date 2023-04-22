package ch.realmtech.game.ecs.component;

import ch.realmtech.game.registery.ItemRegisterEntry;
import com.artemis.Component;

public class ItemComponent extends Component {
    public ItemRegisterEntry itemRegisterEntry;

    public void set(ItemRegisterEntry itemRegisterEntry) {
        this.itemRegisterEntry = itemRegisterEntry;
    }

    @Override
    public String toString() {
        return itemRegisterEntry.toString();
    }
}
