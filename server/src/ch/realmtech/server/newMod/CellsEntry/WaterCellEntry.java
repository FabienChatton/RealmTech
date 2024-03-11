package ch.realmtech.server.newMod.CellsEntry;

import ch.realmtech.server.ecs.component.CellBeingMineComponent;
import ch.realmtech.server.level.cell.CellBehavior;
import ch.realmtech.server.level.cell.Cells;
import ch.realmtech.server.newRegistry.NewCellEntry;
import ch.realmtech.server.sound.SoundManager;

public class WaterCellEntry extends NewCellEntry {
    public WaterCellEntry() {
        super("water", "water-01", CellBehavior.builder(Cells.Layer.GROUND)
                .speedEffect(0.5f)
                .playerWalkSound(0.25f, SoundManager.FOOT_STEP_WATER_1, SoundManager.FOOT_STEP_WATER_2, SoundManager.FOOT_STEP_WATER_3, SoundManager.FOOT_STEP_WATER_4)
                .breakStepNeed(CellBeingMineComponent.INFINITE_MINE)
                .canPlaceCellOnTop(false)
                .build());
    }
}
