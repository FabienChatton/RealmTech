package ch.realmtech.server.mod.items;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.registry.ItemEntry;

public class WoodItemEntry extends ItemEntry {
    public WoodItemEntry() {
        super("Wood", "buche-01", ItemBehavior.builder()
                .setTimeToBurn(60)
                .build());
    }

    @Override
    public int getId() {
        return 1378568676;
    }
}
