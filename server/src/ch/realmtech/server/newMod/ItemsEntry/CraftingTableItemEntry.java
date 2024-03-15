package ch.realmtech.server.newMod.ItemsEntry;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.newRegistry.NewItemEntry;

public class CraftingTableItemEntry extends NewItemEntry {
    public CraftingTableItemEntry() {
        super("CraftingTable", "table-craft-01", ItemBehavior.builder()
                .placeCell("realmtech.cells.CraftingTable")
                .build());
    }

    @Override
    public int getId() {
        return 129156771;
    }
}
