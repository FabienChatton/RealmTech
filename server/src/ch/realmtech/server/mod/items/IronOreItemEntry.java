package ch.realmtech.server.mod.items;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.registry.ItemEntry;

public class IronOreItemEntry extends ItemEntry {
    public IronOreItemEntry() {
        super("IronOre", "iron-ore-01", ItemBehavior.builder()
                .build());
    }

}
