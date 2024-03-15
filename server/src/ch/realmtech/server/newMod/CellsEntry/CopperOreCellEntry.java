package ch.realmtech.server.newMod.CellsEntry;

import ch.realmtech.server.item.ItemType;
import ch.realmtech.server.level.cell.CellBehavior;
import ch.realmtech.server.level.cell.Cells;
import ch.realmtech.server.newRegistry.NewCellEntry;

public class CopperOreCellEntry extends NewCellEntry {
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
