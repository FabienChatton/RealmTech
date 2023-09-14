package ch.realmtechCommuns.ecs.component;

import ch.realmtechCommuns.registery.ItemRegisterEntry;
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
