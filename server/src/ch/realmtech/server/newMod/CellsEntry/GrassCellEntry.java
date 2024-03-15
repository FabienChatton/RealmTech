package ch.realmtech.server.newMod.CellsEntry;

import ch.realmtech.server.ecs.component.CellBeingMineComponent;
import ch.realmtech.server.level.cell.CellBehavior;
import ch.realmtech.server.level.cell.Cells;
import ch.realmtech.server.newRegistry.NewCellEntry;
import ch.realmtech.server.sound.SoundManager;

public class GrassCellEntry extends NewCellEntry {
    public GrassCellEntry() {
        super("Grass", "grass-01", CellBehavior.builder(Cells.Layer.GROUND)
                .playerWalkSound(1f, SoundManager.FOOT_STEP_GRASS_1, SoundManager.FOOT_STEP_GRASS_2, SoundManager.FOOT_STEP_GRASS_3, SoundManager.FOOT_STEP_GRASS_4, SoundManager.FOOT_STEP_GRASS_5, SoundManager.FOOT_STEP_GRASS_6, SoundManager.FOOT_STEP_GRASS_7, SoundManager.FOOT_STEP_GRASS_8, SoundManager.FOOT_STEP_GRASS_9)
                .breakStepNeed(CellBeingMineComponent.INFINITE_MINE)
                .tiledTexture(1, 1)
                .build());
    }

    @Override
    public int getId() {
        return 1383095341;
    }
}
