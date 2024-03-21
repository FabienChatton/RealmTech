package ch.realmtech.server.mod.items;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.registry.ItemEntry;

public class CoalOreItemEntry extends ItemEntry {
    public CoalOreItemEntry() {
        super("CoalOre", "coal-ore-01", ItemBehavior.builder()
                .build());
    }
}
