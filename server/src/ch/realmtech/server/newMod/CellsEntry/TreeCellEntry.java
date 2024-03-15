package ch.realmtech.server.newMod.CellsEntry;

import ch.realmtech.server.item.ItemType;
import ch.realmtech.server.level.cell.CellBehavior;
import ch.realmtech.server.level.cell.Cells;
import ch.realmtech.server.level.cell.CreatePhysiqueBody;
import ch.realmtech.server.newRegistry.NewCellEntry;

public class TreeCellEntry extends NewCellEntry {
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
