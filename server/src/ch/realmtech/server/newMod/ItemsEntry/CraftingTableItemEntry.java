package ch.realmtech.server.newMod.ItemsEntry;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.newRegistry.NewItemEntry;

public class CraftingTableItemEntry extends NewItemEntry {
    public CraftingTableItemEntry() {
        super("craftingTable", "table-craft-01", ItemBehavior.builder()
                .placeCell("realmtech.cells.craftingTable")
                .build());
    }

    @Override
    public int getId() {
        return 129156771;
    }
}
