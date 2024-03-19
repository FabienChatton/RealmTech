package ch.realmtech.server.mod.items;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.registry.ItemEntry;

public class CopperIngotItemEntry extends ItemEntry {
    public CopperIngotItemEntry() {
        super("CopperIngot", "copper-ingot-01", ItemBehavior.builder()
                .build());
    }

    @Override
    public int getId() {
        return -229826331;
    }
}
