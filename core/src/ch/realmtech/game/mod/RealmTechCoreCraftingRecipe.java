package ch.realmtech.game.mod;

import ch.realmtech.game.craft.CraftPatternFix;
import ch.realmtech.game.craft.CraftPatternShape;
import ch.realmtech.game.craft.CraftPatternShapeless;
import ch.realmtech.game.registery.CraftingRecipeEntry;
import ch.realmtech.game.registery.InfRegistryAnonyme;

import static ch.realmtech.game.craft.CraftPatternFix.CraftPatternArgs;

public class RealmTechCoreCraftingRecipe {

    public static void initCraftingRecipe(final InfRegistryAnonyme<CraftingRecipeEntry> registry) {
        registry.add(new CraftPatternFix(RealmTechCoreMod.SANDALES_ITEM, 2, new char[]{
                ' ', 'a', ' ',
                ' ', ' ', ' ',
                ' ', ' ', ' '
        }, new CraftPatternArgs('a', RealmTechCoreMod.PLANCHE_CELL_ITEM.itemRegisterEntry())));

        registry.add(new CraftPatternShapeless(RealmTechCoreMod.PLANCHE_CELL_ITEM.itemRegisterEntry(), 4, RealmTechCoreMod.BUCHE_ITEM));

        registry.add(new CraftPatternShape(RealmTechCoreMod.STICK_ITEM, 4, new char[][]{
                new char[]{'a'},
                new char[]{'a'}
        }, new CraftPatternArgs('a', RealmTechCoreMod.PLANCHE_CELL_ITEM.itemRegisterEntry())));

        registry.add(new CraftPatternShape(RealmTechCoreMod.CRAFTING_TABLE.itemRegisterEntry(), new char[][]{
                new char[]{'a', 'a'},
                new char[]{'a', 'a'}
        }, new CraftPatternArgs('a', RealmTechCoreMod.PLANCHE_CELL_ITEM.itemRegisterEntry())));

        registry.add(new CraftPatternFix(RealmTechCoreMod.CHEST.itemRegisterEntry(), new char[]{
                'a', 'a', 'a',
                'a', ' ', 'a',
                'a', 'a', 'a'
        }, new CraftPatternArgs('a', RealmTechCoreMod.PLANCHE_CELL_ITEM.itemRegisterEntry())));
    }
}
