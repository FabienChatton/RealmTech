package ch.realmtech.server.mod.cells;

import ch.realmtech.server.ecs.component.CellBeingMineComponent;
import ch.realmtech.server.level.cell.CellBehavior;
import ch.realmtech.server.level.cell.Cells;
import ch.realmtech.server.registry.CellEntry;
import ch.realmtech.server.sound.SoundManager;

public class SandCellEntry extends CellEntry {
    public SandCellEntry() {
        super("Sand", "sand-01", CellBehavior.builder(Cells.Layer.GROUND)
                .playerWalkSound(0.25f, SoundManager.FOOT_STEP_SAND_1, SoundManager.FOOT_STEP_SAND_2, SoundManager.FOOT_STEP_SAND_3, SoundManager.FOOT_STEP_SAND_4, SoundManager.FOOT_STEP_SAND_5, SoundManager.FOOT_STEP_SAND_6, SoundManager.FOOT_STEP_SAND_7, SoundManager.FOOT_STEP_SAND_8, SoundManager.FOOT_STEP_SAND_9, SoundManager.FOOT_STEP_SAND_10)
                .breakStepNeed(CellBeingMineComponent.INFINITE_MINE)
                .tiledTexture(2, 2)
                .build());
    }

    @Override
    public int getId() {
        return -1063421139;
    }
}
