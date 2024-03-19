package ch.realmtech.server.mod.items;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.registry.ItemEntry;

public class CraftingTableItemEntry extends ItemEntry {
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
