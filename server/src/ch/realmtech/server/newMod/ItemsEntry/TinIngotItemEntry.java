package ch.realmtech.server.newMod.ItemsEntry;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.newRegistry.NewItemEntry;

public class TinIngotItemEntry extends NewItemEntry {
    public TinIngotItemEntry() {
        super("TinIngot", "tin-ingot-01", ItemBehavior.builder()
                .build());
    }

    @Override
    public int getId() {
        return -5490089;
    }
}
