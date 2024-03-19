package ch.realmtech.server.mod.items;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.registry.ItemEntry;

public class ChestItemEntry extends ItemEntry {
    public ChestItemEntry() {
        super("Chest", "chest-01", ItemBehavior.builder()
                .placeCell("realmtech.cells.Chest")
                .build());
    }

    @Override
    public int getId() {
        return 1379107192;
    }
}
