package ch.realmtech.server.mod.cells;

import ch.realmtech.server.item.ItemType;
import ch.realmtech.server.level.cell.CellBehavior;
import ch.realmtech.server.level.cell.Cells;
import ch.realmtech.server.registry.CellEntry;

public class StoneOreCellEntry extends CellEntry {
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
