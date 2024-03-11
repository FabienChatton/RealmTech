package ch.realmtech.server.newMod.ItemsEntry;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.newRegistry.NewItemEntry;

public class ChestItemEntry extends NewItemEntry {
    public ChestItemEntry() {
        super("chest", "chest-01", ItemBehavior.builder()
                .placeCell("realmtech.cells.chest")
                .build());
    }
}
