package ch.realmtech.server.mod;

import ch.realmtech.server.craft.CraftPatternFix;
import ch.realmtech.server.craft.CraftPatternShape;
import ch.realmtech.server.craft.CraftPatternShapeless;
import ch.realmtech.server.craft.FurnacePatternShape;
import ch.realmtech.server.registery.CraftingRecipeEntry;
import ch.realmtech.server.registery.InfRegistryAnonyme;

import static ch.realmtech.server.craft.CraftPatternFix.CraftPatternArgs;

public class RealmTechCoreCraftingRecipe {

    public static void initCraftingRecipe(final InfRegistryAnonyme<CraftingRecipeEntry> registry) {
        registry.add(new CraftPatternFix(RealmTechCoreMod.SANDALES_ITEM, 2, new char[]{
                ' ', 'a', ' ',
                ' ', ' ', ' ',
                ' ', ' ', ' '
        }, new CraftPatternArgs('a', RealmTechCoreMod.PLANCHE_CELL_ITEM.itemRegisterEntry())));

        registry.add(new CraftPatternShapeless(RealmTechCoreMod.PLANCHE_CELL_ITEM.itemRegisterEntry(), 4, RealmTechCoreMod.BUCHE_ITEM));

        registry.add(new CraftPatternShape(RealmTechCoreMod.STICK_ITEM, 4, new char[][]{
                {'a'},
                {'a'}
        }, new CraftPatternArgs('a', RealmTechCoreMod.PLANCHE_CELL_ITEM.itemRegisterEntry())));

        registry.add(new CraftPatternShape(RealmTechCoreMod.CRAFTING_TABLE.itemRegisterEntry(), new char[][]{
                {'a', 'a'},
                {'a', 'a'}
        }, new CraftPatternArgs('a', RealmTechCoreMod.PLANCHE_CELL_ITEM.itemRegisterEntry())));

        registry.add(new CraftPatternShape(RealmTechCoreMod.CHEST.itemRegisterEntry(), new char[][]{
                {'a', 'a', 'a'},
                {'a', ' ', 'a'},
                {'a', 'a', 'a'}
        }, new CraftPatternArgs('a', RealmTechCoreMod.PLANCHE_CELL_ITEM.itemRegisterEntry())));

        registry.add(new CraftPatternShape(RealmTechCoreMod.PIOCHE_BOIS_ITEM, new char[][]{
                {'a', 'a', 'a'},
                {' ', 'b', ' '},
                {' ', 'b', ' '}
        }, new CraftPatternArgs('a', RealmTechCoreMod.PLANCHE_CELL_ITEM.itemRegisterEntry()),
                new CraftPatternArgs('b', RealmTechCoreMod.STICK_ITEM)));

        registry.add(new CraftPatternShape(RealmTechCoreMod.PELLE_BOIS_ITEM, new char[][]{
                {' ', 'a', ' '},
                {' ', 'b', ' '},
                {' ', 'b', ' '}
        }, new CraftPatternArgs('a', RealmTechCoreMod.PLANCHE_CELL_ITEM.itemRegisterEntry()),
                new CraftPatternArgs('b', RealmTechCoreMod.STICK_ITEM)));

        registry.add(new CraftPatternShape(RealmTechCoreMod.PIOCHE_STONE_ITEM, new char[][]{
                {'a', 'a', 'a'},
                {' ', 'b', ' '},
                {' ', 'b', ' '}
        }, new CraftPatternArgs('a', RealmTechCoreMod.STONE.itemRegisterEntry()),
                new CraftPatternArgs('b', RealmTechCoreMod.STICK_ITEM)));

        registry.add(new CraftPatternShape(RealmTechCoreMod.PELLE_STONE_ITEM, new char[][]{
                {' ', 'a', ' '},
                {' ', 'b', ' '},
                {' ', 'b', ' '}
        }, new CraftPatternArgs('a', RealmTechCoreMod.STONE.itemRegisterEntry()),
                new CraftPatternArgs('b', RealmTechCoreMod.STICK_ITEM)));

        registry.add(new CraftPatternShape(RealmTechCoreMod.TORCH.itemRegisterEntry(), 4, new char[][] {
                {'c'},
                {'s'}
        }, new CraftPatternArgs('c', RealmTechCoreMod.COAL), new CraftPatternArgs('s', RealmTechCoreMod.STICK_ITEM)));

        registry.add(new CraftPatternShape(RealmTechCoreMod.FURNACE.itemRegisterEntry(), new char[][]{
                {'a', 'a', 'a'},
                {'a', ' ', 'a'},
                {'a', 'a', 'a'}
        }, new CraftPatternArgs('a', RealmTechCoreMod.STONE.itemRegisterEntry())));

        registry.add(new CraftPatternShape(RealmTechCoreMod.ENERGY_BATTERY.itemRegisterEntry(), new char[][]{
                {'p', 'c', 'p'},
                {'p', 'c', 'p'},
                {'p', 'c', 'p'},
        }, new CraftPatternArgs('p', RealmTechCoreMod.PLANCHE_CELL_ITEM.itemRegisterEntry()),
                new CraftPatternArgs('c', RealmTechCoreMod.COPPER_INGOT)));

        registry.add(new CraftPatternShape(RealmTechCoreMod.ENERGY_CABLE.itemRegisterEntry(), 12, new char[][]{
                {'c', 'c', 'c'}
        }, new CraftPatternArgs('c', RealmTechCoreMod.COPPER_INGOT)));

        registry.add(new CraftPatternShape(RealmTechCoreMod.WRENCH, new char[][]{
                {'a', ' ', 'a'},
                {' ', 'a', ' '},
                {' ', 'a', ' '},
        }, new CraftPatternArgs('a', RealmTechCoreMod.IRON_INGOT)));

        registry.add(new CraftPatternShape(RealmTechCoreMod.ENERGY_GENERATOR.itemRegisterEntry(), new char[][]{
                {'s', 's', 's'},
                {'s', 'b', 's'},
                {'s', 's', 's'},
        }, new CraftPatternArgs('s', RealmTechCoreMod.STONE.itemRegisterEntry()),
                new CraftPatternArgs('b', RealmTechCoreMod.ENERGY_BATTERY.itemRegisterEntry())));

        registry.add(new CraftPatternShapeless(RealmTechCoreMod.ENERGY_GENERATOR.itemRegisterEntry(),
                RealmTechCoreMod.FURNACE.itemRegisterEntry(),
                RealmTechCoreMod.ENERGY_BATTERY.itemRegisterEntry()
        ));
    }

    public static void initFurnaceRecipe(InfRegistryAnonyme<CraftingRecipeEntry> registry) {
        registry.add(new FurnacePatternShape(RealmTechCoreMod.SANDALES_ITEM, 100, new char[][]{
                {'a'}
        }, new CraftPatternArgs('a', RealmTechCoreMod.BUCHE_ITEM)));

        registry.add(new FurnacePatternShape(RealmTechCoreMod.TIN_INGOT, 100, new char[][]{
                {'a'}
        }, new CraftPatternArgs('a', RealmTechCoreMod.TIN_ORE.itemRegisterEntry())));

        registry.add(new FurnacePatternShape(RealmTechCoreMod.COPPER_INGOT, 100, new char[][]{
                {'a'}
        }, new CraftPatternArgs('a', RealmTechCoreMod.COPPER_ORE.itemRegisterEntry())));

        registry.add(new FurnacePatternShape(RealmTechCoreMod.GOLD_INGOT, 100, new char[][]{
                {'a'}
        }, new CraftPatternArgs('a', RealmTechCoreMod.GOLD_ORE.itemRegisterEntry())));

        registry.add(new FurnacePatternShape(RealmTechCoreMod.IRON_INGOT, 100, new char[][]{
                {'a'}
        }, new CraftPatternArgs('a', RealmTechCoreMod.IRON_ORE.itemRegisterEntry())));
    }
}
