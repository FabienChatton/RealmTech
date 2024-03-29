package ch.realmtech.server.mod.items;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.registry.ItemEntry;

public class TinIngotItemEntry extends ItemEntry {
    public TinIngotItemEntry() {
        super("TinIngot", "tin-ingot-01", ItemBehavior.builder()
                .build());
    }

    @Override
    public int getId() {
        return -5490089;
    }
}
