package ch.realmtechServer.ecs.component;

import ch.realmtechServer.registery.ItemRegisterEntry;
import com.artemis.Component;

import java.util.UUID;

public class ItemComponent extends Component {
    public ItemRegisterEntry itemRegisterEntry;
    public UUID uuid;

    public void set(ItemRegisterEntry itemRegisterEntry, UUID uuid) {
        this.itemRegisterEntry = itemRegisterEntry;
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return String.format("%s %s", itemRegisterEntry, uuid);
    }
}
