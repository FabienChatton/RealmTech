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

    public final static Registry<CellRegisterEntry> REALM_TECH_CORE_CELL_REGISTRY = Registry.create(MOD_ID);
    public final static Registry<ItemRegisterEntry> REALM_TECH_CORE_ITEM_REGISTRY = Registry.create(MOD_ID);
    public final static RegistryAnonyme<CraftingRecipeEntry> REALM_TECH_CORE_CRAFTING_RECIPE_ENTRY = RegistryAnonyme.create(MOD_ID);

    @Override
    public void initialize() {
        RealmTechCoreCell.initCell(REALM_TECH_CORE_CELL_REGISTRY, context);
        RealmTechCoreItem.initItem(REALM_TECH_CORE_ITEM_REGISTRY, textureAtlas);
        RealmTechCoreCraftingRecipe.initCraftingRecipe(REALM_TECH_CORE_CRAFTING_RECIPE_ENTRY);
    }


}
