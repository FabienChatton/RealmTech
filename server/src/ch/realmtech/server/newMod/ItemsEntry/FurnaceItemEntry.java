package ch.realmtech.server.newMod.ItemsEntry;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.newRegistry.NewItemEntry;

public class FurnaceItemEntry extends NewItemEntry {
    public FurnaceItemEntry() {
        super("furnace", "furnace-01", ItemBehavior.builder()
                .placeCell("realmtech.cells.furnace")
                .build());
    }
}
