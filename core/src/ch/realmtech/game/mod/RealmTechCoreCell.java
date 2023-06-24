package ch.realmtech.game.mod;

import ch.realmtech.game.ecs.system.SoundManager;
import ch.realmtech.game.item.ItemType;
import ch.realmtech.game.level.cell.CellBehavior;
import ch.realmtech.game.registery.CellRegisterEntry;
import ch.realmtech.game.registery.Registry;

import static ch.realmtech.game.level.cell.Cells.Layer;

public class RealmTechCoreCell {
    public final static CellRegisterEntry GRASS_CELL = new CellRegisterEntry(
            "grass-01",
            new CellBehavior.Builder(Layer.GROUND)
                    .playerWalkSound(SoundManager.FOOT_STEP_GRASS_2, 1f)
                    .build()
    );
    public final static CellRegisterEntry SAND_CELL = new CellRegisterEntry(
            "sand-01",
            new CellBehavior.Builder(Layer.GROUND)
                    .playerWalkSound(SoundManager.FOOT_STEP_SAND_1, 0.25f)
                    .build()
    );
    public final static CellRegisterEntry WATER_CELL = new CellRegisterEntry(
            "water-01",
            new CellBehavior.Builder(Layer.GROUND)
                    .speedEffect(0.5f)
                    .playerWalkSound(SoundManager.FOOT_STEP_WATER_1, 0.25f)
                    .build()
    );
    public final static CellRegisterEntry COPPER_ORE = new CellRegisterEntry(
            "copper-ore-1",
            new CellBehavior.Builder(Layer.GROUND_DECO)
                    .breakWith(ItemType.PIOCHE)
                    .build()
    );


    static void initCell (Registry<CellRegisterEntry> registry) {
        registry.add("grass", GRASS_CELL);
        registry.add("sand", SAND_CELL);
        registry.add("water", WATER_CELL);
        registry.add("copperOre", COPPER_ORE);
    }
}
