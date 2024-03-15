package ch.realmtech.server.newMod.ItemsEntry;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.newRegistry.NewItemEntry;

public class ChestItemEntry extends NewItemEntry {
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
