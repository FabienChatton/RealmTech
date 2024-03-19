package ch.realmtech.server.mod.items;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.item.ItemType;
import ch.realmtech.server.registry.ItemEntry;

public class NoItemEntry extends ItemEntry {
    public NoItemEntry() {
        super("Noitem", "default-texture", ItemBehavior.builder()
                .setItemType(ItemType.HAND).build());
    }

    @Override
    public int getId() {
        return 124155069;
    }
}
