package ch.realmtech.server.newMod.ItemsEntry;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.newRegistry.NewItemEntry;

public class IronIngotItemEntry extends NewItemEntry {
    public IronIngotItemEntry() {
        super("IronIngot", "iron-ingot-01", ItemBehavior.builder()
                .build());
    }

    @Override
    public int getId() {
        return -1344793514;
    }
}