package ch.realmtech.game.mod;

import ch.realmtech.game.item.ItemType;
import ch.realmtech.game.level.cell.CellBehavior;
import ch.realmtech.game.registery.CellRegisterEntry;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import static ch.realmtech.game.mod.RealmTechCoreMod.REALM_TECH_CORE_CELL_REGISTRY;

public class RealmTechCoreCell {

    public final static String GRASS_CELL = "cell.grass";
    public final static String SAND_CELL = "cell.sand";
    public final static String WATER_CELL = "cell.water";

    static void initCell(TextureAtlas textureAtlas) {
        REALM_TECH_CORE_CELL_REGISTRY.put(GRASS_CELL, new CellRegisterEntry(
                textureAtlas.findRegion("grass-01"),
                new CellBehavior.Builder()
                        .breakWith(ItemType.PELLE)
                        .build()
        ));

        REALM_TECH_CORE_CELL_REGISTRY.put(SAND_CELL, new CellRegisterEntry(
                textureAtlas.findRegion("sand-01"),
                new CellBehavior.Builder()
                        .breakWith(ItemType.PELLE)
                        .build()
        ));

        REALM_TECH_CORE_CELL_REGISTRY.put(WATER_CELL, new CellRegisterEntry(
                textureAtlas.findRegion("water-01"),
                new CellBehavior.Builder()
                        .speedEffect(0.5f)
                        .build()
        ));
    }
}
