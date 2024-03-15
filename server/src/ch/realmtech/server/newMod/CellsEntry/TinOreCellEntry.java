package ch.realmtech.server.newMod.CellsEntry;

import ch.realmtech.server.item.ItemType;
import ch.realmtech.server.level.cell.CellBehavior;
import ch.realmtech.server.level.cell.Cells;
import ch.realmtech.server.newRegistry.NewCellEntry;

public class TinOreCellEntry extends NewCellEntry {
    public TinOreCellEntry() {
        super("TinOre", "tin-ore-01", CellBehavior.builder(Cells.Layer.GROUND_DECO)
                .breakWith(ItemType.PICKAXE, "realmtech.items.TinOre")
                .canPlaceCellOnTop(false)
                .build());
    }

    @Override
    public int getId() {
        return 290502642;
    }
}
