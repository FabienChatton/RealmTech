package ch.realmtech.game.mod;

import ch.realmtech.game.craft.CraftPattern;
import ch.realmtech.game.registery.CraftingRecipeEntry;
import ch.realmtech.game.registery.InfRegistryAnonyme;

import static ch.realmtech.game.craft.CraftPattern.CraftPatternArgs;

public class RealmTechCoreCraftingRecipe {

    public static void initCraftingRecipe(final InfRegistryAnonyme<CraftingRecipeEntry> registry) {
        registry.add(new CraftPattern(RealmTechCoreItem.SANDALES_ITEM, new char[]{
                'b', ' ', ' ',
                ' ', ' ', ' ',
                ' ', ' ', ' '
        }, new CraftPatternArgs('b', RealmTechCoreItem.BUCHE_ITEM)));
    }
}
