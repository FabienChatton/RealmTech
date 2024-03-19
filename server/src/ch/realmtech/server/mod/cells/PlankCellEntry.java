package ch.realmtech.server.mod.cells;

import ch.realmtech.server.item.ItemType;
import ch.realmtech.server.level.cell.CellBehavior;
import ch.realmtech.server.level.cell.Cells;
import ch.realmtech.server.registry.CellEntry;
import ch.realmtech.server.sound.SoundManager;

public class PlankCellEntry extends CellEntry {
    public PlankCellEntry() {
        super("Plank", "plank-cell-01", CellBehavior.builder(Cells.Layer.BUILD)
                .breakWith(ItemType.HAND)
                .playerWalkSound(1f, SoundManager.FOOT_STEP_WOOD_1, SoundManager.FOOT_STEP_WOOD_2, SoundManager.FOOT_STEP_WOOD_3, SoundManager.FOOT_STEP_WOOD_4, SoundManager.FOOT_STEP_WOOD_5, SoundManager.FOOT_STEP_WOOD_6, SoundManager.FOOT_STEP_WOOD_7, SoundManager.FOOT_STEP_WOOD_8, SoundManager.FOOT_STEP_WOOD_9)
                .dropOnBreak("realmtech.items.Plank")
                .build());
    }

    @Override
    public int getId() {
        return 1235390862;
    }
}
