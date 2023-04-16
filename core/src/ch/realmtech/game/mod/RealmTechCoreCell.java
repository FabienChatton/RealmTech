package ch.realmtech.game.mod;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.system.SoundManager;
import ch.realmtech.game.item.ItemType;
import ch.realmtech.game.level.cell.CellBehavior;
import ch.realmtech.game.registery.CellRegisterEntry;

import static ch.realmtech.game.mod.RealmTechCoreMod.REALM_TECH_CORE_CELL_REGISTRY;

public class RealmTechCoreCell {

    public final static String GRASS_CELL = "cell.grass";
    public final static String SAND_CELL = "cell.sand";
    public final static String WATER_CELL = "cell.water";

    static void initCell(RealmTech context) {
        REALM_TECH_CORE_CELL_REGISTRY.put(GRASS_CELL, new CellRegisterEntry(
                context.getTextureAtlas().findRegion("grass-01"),
                new CellBehavior.Builder()
                        .breakWith(ItemType.PELLE)
                        .playerWalkSound(context.getAssetManager().get(SoundManager.FOOT_STEP_GRASS_2), 1f)
                        .build()
        ));

        REALM_TECH_CORE_CELL_REGISTRY.put(SAND_CELL, new CellRegisterEntry(
                context.getTextureAtlas().findRegion("sand-01"),
                new CellBehavior.Builder()
                        .breakWith(ItemType.PELLE)
                        .playerWalkSound(context.getAssetManager().get(SoundManager.FOOT_STEP_SAND_1),0.25f)
                        .build()
        ));

        REALM_TECH_CORE_CELL_REGISTRY.put(WATER_CELL, new CellRegisterEntry(
                context.getTextureAtlas().findRegion("water-01"),
                new CellBehavior.Builder()
                        .speedEffect(0.5f)
                        .playerWalkSound(context.getAssetManager().get(SoundManager.FOOT_STEP_WATER_1), 0.25f)
                        .build()
        ));
    }
}
