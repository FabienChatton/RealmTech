package ch.realmtech.server.newMod.CellsEntry;

import ch.realmtech.server.item.ItemType;
import ch.realmtech.server.level.cell.CellBehavior;
import ch.realmtech.server.level.cell.Cells;
import ch.realmtech.server.newRegistry.NewCellEntry;

public class StoneOreCellEntry extends NewCellEntry {
    public StoneOreCellEntry() {
        super("StoneOre", "stone-ore-01", CellBehavior.builder(Cells.Layer.GROUND_DECO)
                .breakWith(ItemType.PICKAXE, "realmtech.items.StoneOre")
                .canPlaceCellOnTop(false)
                .build());
    }

    @Override
    public int getId() {
        return 1394250460;
    }
}
