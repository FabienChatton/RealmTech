package ch.realmtech.game.mod;

import ch.realmtech.RealmTech;
import ch.realmtech.game.registery.CellRegisterEntry;
import ch.realmtech.game.registery.ItemRegisterEntry;
import ch.realmtech.game.registery.Registry;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class RealmTechCoreMod extends ModInitializerManager {
    @Wire
    private TextureAtlas textureAtlas;
    @Wire(name = "context")
    private RealmTech context;

    public final static String MOD_ID = "realmtech";

    public final static Registry<CellRegisterEntry> REALM_TECH_CORE_CELL_REGISTRY = Registry.create(MOD_ID);
    public final static Registry<ItemRegisterEntry> REALM_TECH_CORE_ITEM_REGISTRY = Registry.create(MOD_ID);

    @Override
    public void initialize() {
        RealmTechCoreCell.initCell(context);
        RealmTechCoreItem.initItem(textureAtlas);
    }


}
