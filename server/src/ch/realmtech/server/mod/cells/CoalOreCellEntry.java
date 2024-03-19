package ch.realmtech.server.mod.cells;

import ch.realmtech.server.item.ItemType;
import ch.realmtech.server.level.cell.CellBehavior;
import ch.realmtech.server.level.cell.Cells;
import ch.realmtech.server.registry.CellEntry;

public class CoalOreCellEntry extends CellEntry {
    public CoalOreCellEntry() {
        super("CoalOre", "coal-ore-01", CellBehavior.builder(Cells.Layer.GROUND_DECO)
                .breakWith(ItemType.PICKAXE, "realmtech.items.Coal")
                .canPlaceCellOnTop(false)
                .build());
    }

    @Override
    public int getId() {
        return -1626413438;
    }
}
