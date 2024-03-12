package ch.realmtech.server.newMod.CellsEntry;

import ch.realmtech.server.item.ItemType;
import ch.realmtech.server.level.cell.CellBehavior;
import ch.realmtech.server.level.cell.Cells;
import ch.realmtech.server.newRegistry.NewCellEntry;

public class IronOreCellEntry extends NewCellEntry {
    public IronOreCellEntry() {
        super("ironOre", "iron-ore-01", CellBehavior.builder(Cells.Layer.GROUND_DECO)
                .breakWith(ItemType.PICKAXE, "realmtech.items.ironOre")
                .canPlaceCellOnTop(false)
                .build());
    }

    @Override
    public int getId() {
        return -497482319;
    }
}
