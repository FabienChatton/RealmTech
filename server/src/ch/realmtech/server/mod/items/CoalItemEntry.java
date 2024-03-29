package ch.realmtech.server.mod.items;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.registry.ItemEntry;

public class CoalItemEntry extends ItemEntry {
    public CoalItemEntry() {
        super("Coal", "coal-01", ItemBehavior.builder()
                .setTimeToBurn(1000)
                .build());
    }

    @Override
    public int getId() {
        return -1063884736;
    }
}
