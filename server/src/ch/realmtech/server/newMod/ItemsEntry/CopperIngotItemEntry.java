package ch.realmtech.server.newMod.ItemsEntry;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.newRegistry.NewItemEntry;

public class CopperIngotItemEntry extends NewItemEntry {
    public CopperIngotItemEntry() {
        super("copperIngot", "copper-ingot-01", ItemBehavior.builder()
                .build());
    }

    @Override
    public int getId() {
        return -229826331;
    }
}
