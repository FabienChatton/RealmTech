package ch.realmtech.server.mod.cells;

import ch.realmtech.server.item.ItemType;
import ch.realmtech.server.level.cell.CellBehavior;
import ch.realmtech.server.level.cell.Cells;
import ch.realmtech.server.registry.CellEntry;

public class CopperOreCellEntry extends CellEntry {
    public CopperOreCellEntry() {
        super("CopperOre", "copper-ore-03", CellBehavior.builder(Cells.Layer.GROUND_DECO)
                .breakWith(ItemType.PICKAXE, "realmtech.items.CopperOre")
                .canPlaceCellOnTop(false)
                .build());
    }

    @Override
    public int getId() {
        return 947251712;
    }
}
