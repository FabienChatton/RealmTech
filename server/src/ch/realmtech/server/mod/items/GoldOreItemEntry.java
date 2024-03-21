package ch.realmtech.server.mod.items;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.registry.ItemEntry;

public class GoldOreItemEntry extends ItemEntry {
    public GoldOreItemEntry() {
        super("GoldOre", "gold-ore-01", ItemBehavior.builder()
                .build());
    }
}
