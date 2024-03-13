package ch.realmtech.server.newMod;

import ch.realmtech.server.newMod.CellsEntry.*;
import ch.realmtech.server.newMod.CraftEntry.*;
import ch.realmtech.server.newMod.ItemsEntry.*;
import ch.realmtech.server.newMod.NewQuest.*;
import ch.realmtech.server.newMod.entityEditFactory.EditEntityFactory;
import ch.realmtech.server.newRegistry.*;

public class NewRealmTechCoreMod implements ModInitializer {
    @Override
    public String getModId() {
        return "realmtech";
    }

    @Override
    public void initializeModRegistry(NewRegistry<?> modRegistry) {
        // cells
        NewRegistry<NewCellEntry> cellsRegistry = NewRegistry.createRegistry(modRegistry, "cells");
        cellsRegistry.addEntry(new ChestCellEntry());
        cellsRegistry.addEntry(new CoalOreCellEntry());
        cellsRegistry.addEntry(new CopperOreCellEntry());
        cellsRegistry.addEntry(new CraftingTableCellEntry());
        cellsRegistry.addEntry(new EnergyBatteryCellEntry());
        cellsRegistry.addEntry(new EnergyCableCellEntry());
        cellsRegistry.addEntry(new EnergyGeneratorCellEntry());
        cellsRegistry.addEntry(new FurnaceCellEntry());
        cellsRegistry.addEntry(new GoldOreCellEntry());
        cellsRegistry.addEntry(new GrassCellEntry());
        cellsRegistry.addEntry(new IronOreCellEntry());
        cellsRegistry.addEntry(new PlankCellEntry());
        cellsRegistry.addEntry(new SandCellEntry());
        cellsRegistry.addEntry(new StoneOreCellEntry());
        cellsRegistry.addEntry(new TinOreCellEntry());
        cellsRegistry.addEntry(new TorchCellEntry());
        cellsRegistry.addEntry(new TreeCellEntry());
        cellsRegistry.addEntry(new WaterCellEntry());

        // items
        NewRegistry<NewItemEntry> itemsRegistry = NewRegistry.createRegistry(modRegistry, "items");
        itemsRegistry.addEntry(new ChestItemEntry());
        itemsRegistry.addEntry(new CoalItemEntry());
        itemsRegistry.addEntry(new CoalOreItemEntry());
        itemsRegistry.addEntry(new CopperIngotItemEntry());
        itemsRegistry.addEntry(new CopperOreItemEntry());
        itemsRegistry.addEntry(new CraftingTableItemEntry());
        itemsRegistry.addEntry(new EnergyBatteryItemEntry());
        itemsRegistry.addEntry(new EnergyCableItemEntry());
        itemsRegistry.addEntry(new EnergyGeneratorItemEntry());
        itemsRegistry.addEntry(new FurnaceItemEntry());
        itemsRegistry.addEntry(new GoldIngotItemEntry());
        itemsRegistry.addEntry(new GoldOreItemEntry());
        itemsRegistry.addEntry(new IronIngotItemEntry());
        itemsRegistry.addEntry(new IronOreItemEntry());
        itemsRegistry.addEntry(new NoItemEntry());
        itemsRegistry.addEntry(new PlankItemEntry());
        itemsRegistry.addEntry(new SandalesItemEntry());
        itemsRegistry.addEntry(new StickItemEntry());
        itemsRegistry.addEntry(new StoneOreItemEntry());
        itemsRegistry.addEntry(new StonePickaxeItemEntry());
        itemsRegistry.addEntry(new StoneShovelItemEntry());
        itemsRegistry.addEntry(new TinIngotItemEntry());
        itemsRegistry.addEntry(new TinOreItemEntry());
        itemsRegistry.addEntry(new TorchItemEntry());
        itemsRegistry.addEntry(new WoodenPickaxeItemEntry());
        itemsRegistry.addEntry(new WoodenShovelItemEntry());
        itemsRegistry.addEntry(new WoodItemEntry());
        itemsRegistry.addEntry(new WrenchItemEntry());

        // craft recipe
        NewRegistry<?> crafts = NewRegistry.createRegistry(modRegistry, "crafts");

        // craft crafting table
        NewRegistry<NewCraftRecipeEntry> craftCraftingTableRegistry = NewRegistry.createRegistry(crafts, "craftingTable", "craftingTableRecipes");
        craftCraftingTableRegistry.addEntry(new PlankCraftEntry());
        craftCraftingTableRegistry.addEntry(new StickCraftEntry());
        craftCraftingTableRegistry.addEntry(new CraftingTableCraftEntry());
        craftCraftingTableRegistry.addEntry(new ChestCraftEntry());
        craftCraftingTableRegistry.addEntry(new WoodenPickaxeCraftEntry());
        craftCraftingTableRegistry.addEntry(new WoodenShovelCraftEntry());
        craftCraftingTableRegistry.addEntry(new StonePickaxeStoneCraftEntry());
        craftCraftingTableRegistry.addEntry(new StoneShovelCraftEntry());
        craftCraftingTableRegistry.addEntry(new TorchCraftEntry());
        craftCraftingTableRegistry.addEntry(new FurnaceCraftEntry());
        craftCraftingTableRegistry.addEntry(new EnergyBatteryCraftEntry());
        craftCraftingTableRegistry.addEntry(new EnergyCableCraftEntry());
        craftCraftingTableRegistry.addEntry(new WrenchCraftEntry());

        // quests
        NewRegistry<NewQuestEntry> questRegistry = NewRegistry.createRegistry(modRegistry, "quests");
        questRegistry.addEntry(new CraftingExpansionQuestEntry());
        questRegistry.addEntry(new FirstCraftQuestEntry());
        questRegistry.addEntry(new FirstEnergyBatteryQuestEntry());
        questRegistry.addEntry(new FirstEnergyCableQuestEntry());
        questRegistry.addEntry(new FirstEnergyGeneratorQuestEntry());
        questRegistry.addEntry(new FirstQuestEntry());
        questRegistry.addEntry(new FirstResourcesQuestEntry());
        questRegistry.addEntry(new FirstToolQuestEntry());
        questRegistry.addEntry(new GetReadyForElectricityQuestEntry());
        questRegistry.addEntry(new KnowYouWorldQuestEntry());
        questRegistry.addEntry(new ThermalExpansionQuestEntry());

        // entity edit factory
        NewRegistry<EditEntityFactory> entityEditFactory = NewRegistry.createRegistry(modRegistry, "editEntity");
        entityEditFactory.addEntry(new EditEntityFactory());
    }
}
