package ch.realmtech.server.newMod.ItemsEntry;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.newRegistry.NewItemEntry;

public class GoldIngotItemEntry extends NewItemEntry {
    public GoldIngotItemEntry() {
        super("GoldIngot", "gold-ingot-01", ItemBehavior.builder()
                .build());
    }

    @Override
    public int getId() {
        return -1612150850;
    }
}
