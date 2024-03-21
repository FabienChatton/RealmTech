package ch.realmtech.server.mod.items;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.registry.ItemEntry;

public class FurnaceItemEntry extends ItemEntry {
    public FurnaceItemEntry() {
        super("Furnace", "furnace-01", ItemBehavior.builder()
                .placeCell("realmtech.cells.Furnace")
                .build());
    }
}
