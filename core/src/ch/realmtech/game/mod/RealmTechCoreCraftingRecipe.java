package ch.realmtech.game.mod;

import ch.realmtech.game.craft.CraftPattern;
import ch.realmtech.game.craft.CraftPatternShape;
import ch.realmtech.game.craft.CraftPatternShapeless;
import ch.realmtech.game.registery.CraftingRecipeEntry;
import ch.realmtech.game.registery.InfRegistryAnonyme;

import static ch.realmtech.game.craft.CraftPattern.CraftPatternArgs;

public class RealmTechCoreCraftingRecipe {

    public static void initCraftingRecipe(final InfRegistryAnonyme<CraftingRecipeEntry> registry) {
        registry.add(new CraftPattern(RealmTechCoreItem.SANDALES_ITEM, 2, new char[] {
                ' ', 'a', ' ',
                ' ', ' ', ' ',
                ' ', ' ', ' '
        }, new CraftPatternArgs('a', RealmTechCoreItem.PLANCHE_ITEM)));

        registry.add(new CraftPatternShapeless(RealmTechCoreItem.PLANCHE_ITEM, 4, RealmTechCoreItem.BUCHE_ITEM));

        registry.add(new CraftPatternShape(RealmTechCoreItem.STICK_ITEM, 4, new char[][] {
                new char[] {'a'},
                new char[] {'a'}
        }, new CraftPatternArgs('a', RealmTechCoreItem.PLANCHE_ITEM)));
    }
}
