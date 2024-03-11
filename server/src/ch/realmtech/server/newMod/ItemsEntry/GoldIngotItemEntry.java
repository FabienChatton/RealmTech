package ch.realmtech.server.newMod.ItemsEntry;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.newRegistry.NewItemEntry;

public class GoldIngotItemEntry extends NewItemEntry {
    public GoldIngotItemEntry() {
        super("goldIngot", "gold-ingot-01", ItemBehavior.builder()
                .build());
    }
}
