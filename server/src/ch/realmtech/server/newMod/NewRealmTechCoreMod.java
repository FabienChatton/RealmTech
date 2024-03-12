package ch.realmtech.server.newMod;

import ch.realmtech.server.newMod.CellsEntry.*;
import ch.realmtech.server.newMod.CraftEntry.PlankCraftEntry;
import ch.realmtech.server.newMod.ItemsEntry.*;
import ch.realmtech.server.newRegistry.NewCellEntry;
import ch.realmtech.server.newRegistry.NewCraftRecipeEntry;
import ch.realmtech.server.newRegistry.NewItemEntry;
import ch.realmtech.server.newRegistry.NewRegistry;

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
        itemsRegistry.addEntry(new LogItemEntry());
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
        NewRegistry<NewCraftRecipeEntry> craftCraftingTableRegistry = NewRegistry.createRegistry(crafts, "craftingTable");
        craftCraftingTableRegistry.addEntry(new PlankCraftEntry());
    }
}
