package ch.realmtech.server.mod.items;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.registry.ItemEntry;

public class StoneOreItemEntry extends ItemEntry {
    public StoneOreItemEntry() {
        super("StoneOre", "stone-ore-01", ItemBehavior.builder()
                .build());
    }
}
