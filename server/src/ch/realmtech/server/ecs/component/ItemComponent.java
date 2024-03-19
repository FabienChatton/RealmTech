package ch.realmtech.server.ecs.component;

import ch.realmtech.server.registry.ItemEntry;
import com.artemis.Component;

public class ItemComponent extends Component {
    public ItemEntry itemRegisterEntry;

    public void set(ItemEntry itemRegisterEntry) {
        this.itemRegisterEntry = itemRegisterEntry;
    }

    @Override
    public String toString() {
        return String.format("%s", itemRegisterEntry);
    }
}
