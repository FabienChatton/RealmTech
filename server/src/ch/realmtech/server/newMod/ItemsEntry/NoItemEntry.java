package ch.realmtech.server.newMod.ItemsEntry;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.item.ItemType;
import ch.realmtech.server.newRegistry.NewItemEntry;

public class NoItemEntry extends NewItemEntry {
    public NoItemEntry() {
        super("noitem", "default-texture", ItemBehavior.builder()
                .setItemType(ItemType.HAND).build());
    }
}
