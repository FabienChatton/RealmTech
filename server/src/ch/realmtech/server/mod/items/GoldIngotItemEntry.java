package ch.realmtech.server.mod.items;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.registry.ItemEntry;

public class GoldIngotItemEntry extends ItemEntry {
    public GoldIngotItemEntry() {
        super("GoldIngot", "gold-ingot-01", ItemBehavior.builder()
                .build());
    }

    @Override
    public int getId() {
        return -1612150850;
    }
}
