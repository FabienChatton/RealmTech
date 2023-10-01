package ch.realmtechServer.mod;

import ch.realmtechServer.craft.CraftPatternFix;
import ch.realmtechServer.craft.CraftPatternShape;
import ch.realmtechServer.craft.CraftPatternShapeless;
import ch.realmtechServer.craft.FurnacePatternShape;
import ch.realmtechServer.registery.CraftingRecipeEntry;
import ch.realmtechServer.registery.InfRegistryAnonyme;

import static ch.realmtechServer.craft.CraftPatternFix.CraftPatternArgs;

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

//        registry.add(new CraftPatternShape(RealmTechCoreMod.CRAFTING_TABLE.itemRegisterEntry(), new char[][]{
//                new char[]{'a', 'a'},
//                new char[]{'a', 'a'}
//        }, new CraftPatternArgs('a', RealmTechCoreMod.PLANCHE_CELL_ITEM.itemRegisterEntry())));
//
//        registry.add(new CraftPatternFix(RealmTechCoreMod.CHEST.itemRegisterEntry(), new char[]{
//                'a', 'a', 'a',
//                'a', ' ', 'a',
//                'a', 'a', 'a'
//        }, new CraftPatternArgs('a', RealmTechCoreMod.PLANCHE_CELL_ITEM.itemRegisterEntry())));

        registry.add(new CraftPatternShape(RealmTechCoreMod.PIOCHE_BOIS_ITEM, new char[][]{
                new char[]{'a', 'a', 'a'},
                new char[]{' ', 'b', ' '},
                new char[]{' ', 'b', ' '}
        }, new CraftPatternArgs('a', RealmTechCoreMod.PLANCHE_CELL_ITEM.itemRegisterEntry()),
                new CraftPatternArgs('b', RealmTechCoreMod.STICK_ITEM)));

        registry.add(new CraftPatternShape(RealmTechCoreMod.PELLE_BOIS_ITEM, new char[][]{
                new char[]{' ', 'a', ' '},
                new char[]{' ', 'b', ' '},
                new char[]{' ', 'b', ' '}
        }, new CraftPatternArgs('a', RealmTechCoreMod.PLANCHE_CELL_ITEM.itemRegisterEntry()),
                new CraftPatternArgs('b', RealmTechCoreMod.STICK_ITEM)));

        registry.add(new CraftPatternShape(RealmTechCoreMod.PIOCHE_STONE_ITEM, new char[][]{
                new char[]{'a', 'a', 'a'},
                new char[]{' ', 'b', ' '},
                new char[]{' ', 'b', ' '}
        }, new CraftPatternArgs('a', RealmTechCoreMod.STONE.itemRegisterEntry()),
                new CraftPatternArgs('b', RealmTechCoreMod.STICK_ITEM)));

        registry.add(new CraftPatternShape(RealmTechCoreMod.PELLE_STONE_ITEM, new char[][]{
                new char[]{' ', 'a', ' '},
                new char[]{' ', 'b', ' '},
                new char[]{' ', 'b', ' '}
        }, new CraftPatternArgs('a', RealmTechCoreMod.STONE.itemRegisterEntry()),
                new CraftPatternArgs('b', RealmTechCoreMod.STICK_ITEM)));

//        registry.add(new CraftPatternShape(RealmTechCoreMod.FURNACE.itemRegisterEntry(), new char[][]{
//                new char[]{'a', 'a', 'a'},
//                new char[]{'a', ' ', 'a'},
//                new char[]{'a', 'a', 'a'}
//        }, new CraftPatternArgs('a', RealmTechCoreMod.STONE.itemRegisterEntry())));
    }

    public static void initFurnaceRecipe(InfRegistryAnonyme<CraftingRecipeEntry> registry) {
        registry.add(new FurnacePatternShape(RealmTechCoreMod.SANDALES_ITEM, 100, new char[][]{
                new char[]{'a'}
        }, new CraftPatternArgs('a', RealmTechCoreMod.BUCHE_ITEM)));

        registry.add(new FurnacePatternShape(RealmTechCoreMod.TIN_INGOT, 100, new char[][]{
                new char[]{'a'}
        }, new CraftPatternArgs('a', RealmTechCoreMod.TIN_ORE.itemRegisterEntry())));

        registry.add(new FurnacePatternShape(RealmTechCoreMod.COPPER_INGOT, 100, new char[][]{
                new char[]{'a'}
        }, new CraftPatternArgs('a', RealmTechCoreMod.COPPER_ORE.itemRegisterEntry())));

        registry.add(new FurnacePatternShape(RealmTechCoreMod.GOLD_INGOT, 100, new char[][]{
                new char[]{'a'}
        }, new CraftPatternArgs('a', RealmTechCoreMod.GOLD_ORE.itemRegisterEntry())));

        registry.add(new FurnacePatternShape(RealmTechCoreMod.IRON_INGOT, 100, new char[][]{
                new char[]{'a'}
        }, new CraftPatternArgs('a', RealmTechCoreMod.IRON_ORE.itemRegisterEntry())));
    }
}