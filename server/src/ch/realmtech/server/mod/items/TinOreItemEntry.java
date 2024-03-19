package ch.realmtech.server.mod.items;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.registry.ItemEntry;

public class TinOreItemEntry extends ItemEntry {
    public TinOreItemEntry() {
        super("TinOre", "tin-ore-01", ItemBehavior.builder()
                .build());
    }

    @Override
    public int getId() {
        return 290502642;
    }
}
