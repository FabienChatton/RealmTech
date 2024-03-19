package ch.realmtech.server.mod.items;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.registry.ItemEntry;

public class PlankItemEntry extends ItemEntry {
    public PlankItemEntry() {
        super("Plank", "plank-02", ItemBehavior.builder()
                .build());
    }
}
