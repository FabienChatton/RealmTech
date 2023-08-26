package ch.realmtech.game.mod;

import ch.realmtech.game.craft.CraftPatternFix;
import ch.realmtech.game.craft.CraftPatternShape;
import ch.realmtech.game.craft.CraftPatternShapeless;
import ch.realmtech.game.craft.FurnacePatternShape;
import ch.realmtech.game.registery.CraftingRecipeEntry;
import ch.realmtech.game.registery.InfRegistryAnonyme;

import static ch.realmtech.game.craft.CraftPatternFix.CraftPatternArgs;
import static ch.realmtech.game.mod.RealmTechCoreMod.*;

public class RealmTechCoreCraftingRecipe {

    public static void initCraftingRecipe(final InfRegistryAnonyme<CraftingRecipeEntry> registry) {
        registry.add(new CraftPatternFix(SANDALES_ITEM, 2, new char[]{
                ' ', 'a', ' ',
                ' ', ' ', ' ',
                ' ', ' ', ' '
        }, new CraftPatternArgs('a', PLANCHE_CELL_ITEM.itemRegisterEntry())));

        registry.add(new CraftPatternShapeless(PLANCHE_CELL_ITEM.itemRegisterEntry(), 4, BUCHE_ITEM));

        registry.add(new CraftPatternShape(STICK_ITEM, 4, new char[][]{
                new char[]{'a'},
                new char[]{'a'}
        }, new CraftPatternArgs('a', PLANCHE_CELL_ITEM.itemRegisterEntry())));

        registry.add(new CraftPatternShape(CRAFTING_TABLE.itemRegisterEntry(), new char[][]{
                new char[]{'a', 'a'},
                new char[]{'a', 'a'}
        }, new CraftPatternArgs('a', PLANCHE_CELL_ITEM.itemRegisterEntry())));

        registry.add(new CraftPatternFix(CHEST.itemRegisterEntry(), new char[]{
                'a', 'a', 'a',
                'a', ' ', 'a',
                'a', 'a', 'a'
        }, new CraftPatternArgs('a', PLANCHE_CELL_ITEM.itemRegisterEntry())));

        registry.add(new CraftPatternShape(PIOCHE_BOIS_ITEM, new char[][]{
                new char[]{'a', 'a', 'a'},
                new char[]{' ', 'b', ' '},
                new char[]{' ', 'b', ' '}
        }, new CraftPatternArgs('a', PLANCHE_CELL_ITEM.itemRegisterEntry()),
                new CraftPatternArgs('b', STICK_ITEM)));

        registry.add(new CraftPatternShape(PELLE_BOIS_ITEM, new char[][]{
                new char[]{' ', 'a', ' '},
                new char[]{' ', 'b', ' '},
                new char[]{' ', 'b', ' '}
        }, new CraftPatternArgs('a', PLANCHE_CELL_ITEM.itemRegisterEntry()),
                new CraftPatternArgs('b', STICK_ITEM)));

        registry.add(new CraftPatternShape(PIOCHE_STONE_ITEM, new char[][]{
                new char[]{'a', 'a', 'a'},
                new char[]{' ', 'b', ' '},
                new char[]{' ', 'b', ' '}
        }, new CraftPatternArgs('a', STONE.itemRegisterEntry()),
                new CraftPatternArgs('b', STICK_ITEM)));

        registry.add(new CraftPatternShape(PELLE_STONE_ITEM, new char[][]{
                new char[]{' ', 'a', ' '},
                new char[]{' ', 'b', ' '},
                new char[]{' ', 'b', ' '}
        }, new CraftPatternArgs('a', STONE.itemRegisterEntry()),
                new CraftPatternArgs('b', STICK_ITEM)));

        registry.add(new CraftPatternShape(FURNACE.itemRegisterEntry(), new char[][]{
                new char[]{'a', 'a', 'a'},
                new char[]{'a', ' ', 'a'},
                new char[]{'a', 'a', 'a'}
        }, new CraftPatternArgs('a', STONE.itemRegisterEntry())));
    }

    public static void initFurnaceRecipe(InfRegistryAnonyme<CraftingRecipeEntry> registry) {
        registry.add(new FurnacePatternShape(SANDALES_ITEM, 100, new char[][]{
                new char[]{'a'}
        }, new CraftPatternArgs('a', BUCHE_ITEM)));

        registry.add(new FurnacePatternShape(TIN_INGOT, 100, new char[][]{
                new char[]{'a'}
        }, new CraftPatternArgs('a', TIN_ORE.itemRegisterEntry())));

        registry.add(new FurnacePatternShape(COPPER_INGOT, 100, new char[][]{
                new char[]{'a'}
        }, new CraftPatternArgs('a', COPPER_ORE.itemRegisterEntry())));

        registry.add(new FurnacePatternShape(GOLD_INGOT, 100, new char[][]{
                new char[]{'a'}
        }, new CraftPatternArgs('a', GOLD_ORE.itemRegisterEntry())));

        registry.add(new FurnacePatternShape(IRON_INGOT, 100, new char[][]{
                new char[]{'a'}
        }, new CraftPatternArgs('a', IRON_ORE.itemRegisterEntry())));
    }
}
