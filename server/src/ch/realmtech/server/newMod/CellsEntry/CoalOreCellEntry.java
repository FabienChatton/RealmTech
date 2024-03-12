package ch.realmtech.server.newMod.CellsEntry;

import ch.realmtech.server.item.ItemType;
import ch.realmtech.server.level.cell.CellBehavior;
import ch.realmtech.server.level.cell.Cells;
import ch.realmtech.server.newRegistry.NewCellEntry;

public class CoalOreCellEntry extends NewCellEntry {
    public CoalOreCellEntry() {
        super("coalOre", "coal-ore-01", CellBehavior.builder(Cells.Layer.GROUND_DECO)
                .breakWith(ItemType.PICKAXE, "realmtech.items.coal")
                .canPlaceCellOnTop(false)
                .build());
    }

    @Override
    public int getId() {
        return -1626413438;
    }
}
