package ch.realmtech.server.newMod.CellsEntry;

import ch.realmtech.server.energy.EnergyCableEditEntity;
import ch.realmtech.server.item.ItemType;
import ch.realmtech.server.level.cell.CellBehavior;
import ch.realmtech.server.level.cell.Cells;
import ch.realmtech.server.newRegistry.NewCellEntry;

public class EnergyCableCellEntry extends NewCellEntry {
    public EnergyCableCellEntry() {
        super("EnergyCable", "energy-cable-01-item", CellBehavior.builder(Cells.Layer.BUILD_DECO)
                .canPlaceCellOnTop(false)
                .breakWith(ItemType.HAND, "realmtech.items.EnergyCable")
                .editEntity(new EnergyCableEditEntity((byte) 0))
                .build());
    }

    @Override
    public int getId() {
        return 235045836;
    }
}