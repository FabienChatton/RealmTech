package ch.realmtech.server.mod.cells;

import ch.realmtech.server.item.ItemType;
import ch.realmtech.server.level.cell.CellBehavior;
import ch.realmtech.server.level.cell.Cells;
import ch.realmtech.server.level.cell.CreatePhysiqueBody;
import ch.realmtech.server.registry.CellEntry;

public class TreeCellEntry extends CellEntry {
    public TreeCellEntry() {
        super("Tree", "tree-06", CellBehavior.builder(Cells.Layer.GROUND_DECO)
                .breakWith(ItemType.HAND, "realmtech.items.Wood")
                .physiqueBody(CreatePhysiqueBody.defaultPhysiqueBody())
                .canPlaceCellOnTop(false)
                .build()
        );
    }

    @Override
    public int getId() {
        return -1063375289;
    }
}
