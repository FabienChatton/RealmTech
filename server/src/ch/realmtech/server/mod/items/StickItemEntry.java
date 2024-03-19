package ch.realmtech.server.mod.items;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.registry.ItemEntry;

public class StickItemEntry extends ItemEntry {
    public StickItemEntry() {
        super("Stick", "stick-02", ItemBehavior.builder()
                .build());
    }

    @Override
    public int getId() {
        return 1394244359;
    }
}
