package ch.realmtech.server.newMod.CellsEntry;

import ch.realmtech.server.item.ItemType;
import ch.realmtech.server.level.cell.CellBehavior;
import ch.realmtech.server.level.cell.Cells;
import ch.realmtech.server.newRegistry.NewCellEntry;

public class GoldOreCellEntry extends NewCellEntry {
    public GoldOreCellEntry() {
        super("goldOre", "gold-ore-01", CellBehavior.builder(Cells.Layer.GROUND_DECO)
                .breakWith(ItemType.PICKAXE, "realmtech.items.goldOre")
                .canPlaceCellOnTop(false)
                .build());
    }
}
