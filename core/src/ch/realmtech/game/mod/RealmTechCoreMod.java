package ch.realmtech.game.mod;

import ch.realmtech.RealmTech;
import ch.realmtech.game.registery.CellRegisterEntry;
import ch.realmtech.game.registery.CraftingRecipeEntry;
import ch.realmtech.game.registery.ItemRegisterEntry;
import ch.realmtech.game.registery.RegistryAnonyme;
import ch.realmtech.game.registery.infRegistry.InfRegistry;
import ch.realmtech.game.registery.infRegistry.InfRegistryAnonyme;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class RealmTechCoreMod extends ModInitializerManager {
    @Wire
    private TextureAtlas textureAtlas;
    @Wire(name = "context")
    private RealmTech context;

    public final static String MOD_ID = "realmtech";

    public final static RegistryAnonyme<CraftingRecipeEntry> REALM_TECH_CORE_CRAFTING_RECIPE_ENTRY = RegistryAnonyme.create(MOD_ID);

    public final static InfRegistry<CellRegisterEntry> CELLS = InfRegistry.create(MOD_ID);
    public final static InfRegistry<ItemRegisterEntry> ITEMS = InfRegistry.create(MOD_ID);
    public final static InfRegistryAnonyme<CraftingRecipeEntry> CRAFT = InfRegistryAnonyme.create();

    @Override
    public void initialize() {
        RealmTechCoreCell.initCell(CELLS);
        RealmTechCoreItem.initItem(ITEMS);
        RealmTechCoreCraftingRecipe.initCraftingRecipe(CRAFT);
    }


}
