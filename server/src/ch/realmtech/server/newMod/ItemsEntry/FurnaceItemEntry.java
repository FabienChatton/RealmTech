package ch.realmtech.server.newMod.ItemsEntry;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.newRegistry.NewItemEntry;

public class FurnaceItemEntry extends NewItemEntry {
    public FurnaceItemEntry() {
        super("Furnace", "furnace-01", ItemBehavior.builder()
                .placeCell("realmtech.cells.Furnace")
                .build());
    }

    @Override
    public int getId() {
        return 1223648783;
    }
}
