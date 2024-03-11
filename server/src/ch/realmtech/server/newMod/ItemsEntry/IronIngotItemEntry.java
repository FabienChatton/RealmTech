package ch.realmtech.server.newMod.ItemsEntry;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.newRegistry.NewItemEntry;

public class IronIngotItemEntry extends NewItemEntry {
    public IronIngotItemEntry() {
        super("ironIngot", "iron-ingot-01", ItemBehavior.builder()
                .build());
    }
}
