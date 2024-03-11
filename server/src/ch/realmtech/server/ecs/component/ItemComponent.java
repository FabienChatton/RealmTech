package ch.realmtech.server.ecs.component;

import ch.realmtech.server.registery.ItemRegisterEntry;
import com.artemis.Component;

public class ItemComponent extends Component {
    public ItemRegisterEntry itemRegisterEntry;

    public void set(ItemRegisterEntry itemRegisterEntry) {
        this.itemRegisterEntry = itemRegisterEntry;
    }

    @Override
    public String toString() {
        return String.format("%s", itemRegisterEntry);
    }
}
