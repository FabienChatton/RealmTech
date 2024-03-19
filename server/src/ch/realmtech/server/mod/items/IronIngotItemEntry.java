package ch.realmtech.server.mod.items;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.registry.ItemEntry;

public class IronIngotItemEntry extends ItemEntry {
    public IronIngotItemEntry() {
        super("IronIngot", "iron-ingot-01", ItemBehavior.builder()
                .build());
    }

    @Override
    public int getId() {
        return -1344793514;
    }
}
