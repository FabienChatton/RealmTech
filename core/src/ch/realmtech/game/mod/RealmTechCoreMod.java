package ch.realmtech.game.mod;

import ch.realmtech.game.item.ItemType;
import ch.realmtech.game.level.cell.CellBehavior;
import ch.realmtech.game.registery.CellRegisterEntry;
import ch.realmtech.game.registery.Registry;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class RealmTechCoreMod extends ModInitializerManager {
    @Wire
    private TextureAtlas textureAtlas;

    public final static String MOD_ID = "realmtech";

    public final static String GRASS = "grass";
    public final static String SAND = "sand";
    public final static String WATER = "water";
    public final static Registry<CellRegisterEntry> REALM_TECH_CORE_CELL_REGISTRY = Registry.create(MOD_ID);

    @Override
    public void initialize() {
        REALM_TECH_CORE_CELL_REGISTRY.put(GRASS, new CellRegisterEntry(
                textureAtlas.findRegion("grass-01"),
                new CellBehavior.Builder()
                        .breakWith(ItemType.PELLE)
                        .build()
        ));

        REALM_TECH_CORE_CELL_REGISTRY.put(SAND, new CellRegisterEntry(
                textureAtlas.findRegion("sand-01"),
                new CellBehavior.Builder()
                        .breakWith(ItemType.PELLE)
                        .build()
        ));

        REALM_TECH_CORE_CELL_REGISTRY.put(WATER, new CellRegisterEntry(
                textureAtlas.findRegion("water-01"),
                new CellBehavior.Builder()
                        .speedEffect(0.5f)
                        .build()
        ));

    }
}
