package ch.realmtech.game.mod;

import ch.realmtech.RealmTech;
import ch.realmtech.game.registery.*;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class RealmTechCoreMod extends ModInitializerManager {
    @Wire
    private TextureAtlas textureAtlas;
    @Wire(name = "context")
    private RealmTech context;

    public final static String MOD_ID = "realmtech";

    public final static Registry<CellRegisterEntry> CELLS = Registry.create(MOD_ID);
    public final static Registry<ItemRegisterEntry> ITEMS = Registry.create(MOD_ID);
    public final static InfRegistryAnonyme<CraftingRecipeEntry> CRAFT = InfRegistryAnonyme.create();

    @Override
    public void initialize() {
        RealmTechCoreCell.initCell(CELLS);
        RealmTechCoreItem.initItem(ITEMS);
        RealmTechCoreCraftingRecipe.initCraftingRecipe(CRAFT);
    }
}
