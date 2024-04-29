package ch.realmtech.crafts;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.craft.CraftResult;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.mod.crafts.craftingtable.CraftingTableCraftEntry;
import ch.realmtech.server.mod.crafts.craftingtable.EnergyBatteryCraftEntry;
import ch.realmtech.server.mod.crafts.craftingtable.PlankCraftEntry;
import ch.realmtech.server.mod.items.*;
import ch.realmtech.server.netty.ConnexionConfig;
import ch.realmtech.server.registry.CraftRecipeEntry;
import ch.realmtech.server.registry.FurnaceCraftShapeless;
import ch.realmtech.server.registry.ItemEntry;
import ch.realmtech.server.registry.RegistryUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TestCraft {
    private static ServerContext serverContext;

    @BeforeAll
    static void startEmulator() throws Exception {
        serverContext = new ServerContext(ConnexionConfig.builder().setSaveName("unitTest").build());
    }

    @AfterAll
    static void endEmulator() throws IOException {
        serverContext.close();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void craftTheory() {
        List<CraftRecipeEntry> craftingTableRecipes = (List<CraftRecipeEntry>) RegistryUtils.findEntries(serverContext.getRootRegistry(), "#craftingTableRecipes");
        for (CraftRecipeEntry craftingTableRecipe : craftingTableRecipes) {
            assertTrue(craftingTableRecipe.craft(craftingTableRecipe.getRequireItems()).isPresent());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void craftTheoryFurnace() {
        List<FurnaceCraftShapeless> craftingTableRecipes = (List<FurnaceCraftShapeless>) RegistryUtils.findEntries(serverContext.getRootRegistry(), "#furnaceRecipes");
        for (CraftRecipeEntry craftingTableRecipe : craftingTableRecipes) {
            assertTrue(craftingTableRecipe.craft(craftingTableRecipe.getRequireItems()).isPresent());
        }
    }

    @Test
    public void mapInventoryToItemRegistryTest() {
        int motherChest = serverContext.getEcsEngineServer().getWorld().create();
        ItemEntry plankItem = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), PlankItemEntry.class);
        serverContext.getSystemsAdminServer().getInventoryManager().createChest(motherChest, UUID.randomUUID(), 3, 3);
        int chestInventoryId = serverContext.getSystemsAdminServer().getInventoryManager().getChestInventoryId(motherChest);
        InventoryComponent chestInventory = serverContext.getSystemsAdminServer().getInventoryManager().getChestInventory(motherChest);

        // 0, 1, 2
        // 3, 4, 5
        // 6, 7, 8
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[0], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[1], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[2], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[3], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));
        // empty plank in center for chest craft
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[5], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[6], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[7], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[8], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));

        List<List<ItemEntry>> mapToItems = serverContext.getSystemsAdminServer().getInventoryManager().mapInventoryToItemRegistry(chestInventoryId);
        List<List<ItemEntry>> mapToItemsExpected = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) {
            mapToItemsExpected.add(i, new ArrayList<>(3));
            for (int j = 0; j < 3; j++) {
                if (i == 1 && j == 1) {
                    mapToItemsExpected.get(i).add(j, null);
                } else {
                    mapToItemsExpected.get(i).add(j, plankItem);
                }
            }
        }

        assertEquals(mapToItemsExpected, mapToItems);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getCraftResultShape() {
        int motherChest = serverContext.getEcsEngineServer().getWorld().create();
        ItemEntry plankItem = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), PlankItemEntry.class);
        ItemEntry chestItem = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), ChestItemEntry.class);
        serverContext.getSystemsAdminServer().getInventoryManager().createChest(motherChest, UUID.randomUUID(), 3, 3);
        int chestInventoryId = serverContext.getSystemsAdminServer().getInventoryManager().getChestInventoryId(motherChest);
        InventoryComponent chestInventory = serverContext.getSystemsAdminServer().getInventoryManager().getChestInventory(motherChest);

        // 0, 1, 2
        // 3, 4, 5
        // 6, 7, 8
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[0], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[1], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[2], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[3], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));
        // empty plank in center for chest craft
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[5], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[6], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[7], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[8], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));

        List<CraftRecipeEntry> craftingTableRecipes = (List<CraftRecipeEntry>) RegistryUtils.findEntries(serverContext.getRootRegistry(), "#craftingTableRecipes");
        List<List<ItemEntry>> mapToItems = serverContext.getSystemsAdminServer().getInventoryManager().mapInventoryToItemRegistry(chestInventoryId);
        Optional<CraftResult> newCraftResult = serverContext.getSystemsAdminServer().getCraftingManager().getNewCraftResult(craftingTableRecipes, mapToItems);

        assertEquals(newCraftResult.get().getItemResult(), chestItem);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void stickCraft1() {
        int motherChest = serverContext.getEcsEngineServer().getWorld().create();
        ItemEntry plankItem = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), PlankItemEntry.class);
        ItemEntry stickItem = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), StickItemEntry.class);
        serverContext.getSystemsAdminServer().getInventoryManager().createChest(motherChest, UUID.randomUUID(), 2, 2);
        int chestInventoryId = serverContext.getSystemsAdminServer().getInventoryManager().getChestInventoryId(motherChest);
        InventoryComponent chestInventory = serverContext.getSystemsAdminServer().getInventoryManager().getChestInventory(motherChest);

        // 0, 1
        // 2, 3
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[0], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[2], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));

        List<CraftRecipeEntry> craftingTableRecipes = (List<CraftRecipeEntry>) RegistryUtils.findEntries(serverContext.getRootRegistry(), "#craftingTableRecipes");
        List<List<ItemEntry>> mapToItems = serverContext.getSystemsAdminServer().getInventoryManager().mapInventoryToItemRegistry(chestInventoryId);
        Optional<CraftResult> newCraftResult = serverContext.getSystemsAdminServer().getCraftingManager().getNewCraftResult(craftingTableRecipes, mapToItems);

        assertEquals(newCraftResult.get().getItemResult(), stickItem);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void craftStick2() {
        int motherChest = serverContext.getEcsEngineServer().getWorld().create();
        ItemEntry plankItem = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), PlankItemEntry.class);
        ItemEntry stickItem = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), StickItemEntry.class);
        serverContext.getSystemsAdminServer().getInventoryManager().createChest(motherChest, UUID.randomUUID(), 2, 2);
        int chestInventoryId = serverContext.getSystemsAdminServer().getInventoryManager().getChestInventoryId(motherChest);
        InventoryComponent chestInventory = serverContext.getSystemsAdminServer().getInventoryManager().getChestInventory(motherChest);

        // 0, 1
        // 2, 3
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[1], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[3], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));

        List<CraftRecipeEntry> craftingTableRecipes = (List<CraftRecipeEntry>) RegistryUtils.findEntries(serverContext.getRootRegistry(), "#craftingTableRecipes");
        List<List<ItemEntry>> mapToItems = serverContext.getSystemsAdminServer().getInventoryManager().mapInventoryToItemRegistry(chestInventoryId);
        Optional<CraftResult> newCraftResult = serverContext.getSystemsAdminServer().getCraftingManager().getNewCraftResult(craftingTableRecipes, mapToItems);

        assertEquals(newCraftResult.get().getItemResult(), stickItem);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void craftStick3() {
        int motherChest = serverContext.getEcsEngineServer().getWorld().create();
        ItemEntry plankItem = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), PlankItemEntry.class);
        ItemEntry stickItem = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), StickItemEntry.class);
        serverContext.getSystemsAdminServer().getInventoryManager().createChest(motherChest, UUID.randomUUID(), 2, 2);
        int chestInventoryId = serverContext.getSystemsAdminServer().getInventoryManager().getChestInventoryId(motherChest);
        InventoryComponent chestInventory = serverContext.getSystemsAdminServer().getInventoryManager().getChestInventory(motherChest);

        // 0, 1
        // 2, 3
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[1], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[2], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[3], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));

        List<CraftRecipeEntry> craftingTableRecipes = (List<CraftRecipeEntry>) RegistryUtils.findEntries(serverContext.getRootRegistry(), "#craftingTableRecipes");
        List<List<ItemEntry>> mapToItems = serverContext.getSystemsAdminServer().getInventoryManager().mapInventoryToItemRegistry(chestInventoryId);
        Optional<CraftResult> newCraftResult = serverContext.getSystemsAdminServer().getCraftingManager().getNewCraftResult(craftingTableRecipes, mapToItems);

        assertFalse(newCraftResult.isPresent());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void craftStick4() {
        int motherChest = serverContext.getEcsEngineServer().getWorld().create();
        ItemEntry plankItem = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), PlankItemEntry.class);
        ItemEntry stickItem = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), StickItemEntry.class);
        serverContext.getSystemsAdminServer().getInventoryManager().createChest(motherChest, UUID.randomUUID(), 3, 3);
        int chestInventoryId = serverContext.getSystemsAdminServer().getInventoryManager().getChestInventoryId(motherChest);
        InventoryComponent chestInventory = serverContext.getSystemsAdminServer().getInventoryManager().getChestInventory(motherChest);

        // 0, 1, 2
        // 3, 4, 5
        // 6, 7, 8
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[0], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[3], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));

        List<CraftRecipeEntry> craftingTableRecipes = (List<CraftRecipeEntry>) RegistryUtils.findEntries(serverContext.getRootRegistry(), "#craftingTableRecipes");
        List<List<ItemEntry>> mapToItems = serverContext.getSystemsAdminServer().getInventoryManager().mapInventoryToItemRegistry(chestInventoryId);
        Optional<CraftResult> newCraftResult = serverContext.getSystemsAdminServer().getCraftingManager().getNewCraftResult(craftingTableRecipes, mapToItems);

        assertEquals(newCraftResult.get().getItemResult(), stickItem);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void craftStick5() {
        int motherChest = serverContext.getEcsEngineServer().getWorld().create();
        ItemEntry plankItem = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), PlankItemEntry.class);
        ItemEntry stickItem = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), StickItemEntry.class);
        serverContext.getSystemsAdminServer().getInventoryManager().createChest(motherChest, UUID.randomUUID(), 3, 3);
        int chestInventoryId = serverContext.getSystemsAdminServer().getInventoryManager().getChestInventoryId(motherChest);
        InventoryComponent chestInventory = serverContext.getSystemsAdminServer().getInventoryManager().getChestInventory(motherChest);

        // 0, 1, 2
        // 3, 4, 5
        // 6, 7, 8
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[5], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[8], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));

        List<CraftRecipeEntry> craftingTableRecipes = (List<CraftRecipeEntry>) RegistryUtils.findEntries(serverContext.getRootRegistry(), "#craftingTableRecipes");
        List<List<ItemEntry>> mapToItems = serverContext.getSystemsAdminServer().getInventoryManager().mapInventoryToItemRegistry(chestInventoryId);
        Optional<CraftResult> newCraftResult = serverContext.getSystemsAdminServer().getCraftingManager().getNewCraftResult(craftingTableRecipes, mapToItems);

        assertEquals(newCraftResult.get().getItemResult(), stickItem);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void craftStick6() {
        int motherChest = serverContext.getEcsEngineServer().getWorld().create();
        ItemEntry plankItem = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), PlankItemEntry.class);
        ItemEntry stickItem = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), StickItemEntry.class);
        serverContext.getSystemsAdminServer().getInventoryManager().createChest(motherChest, UUID.randomUUID(), 3, 3);
        int chestInventoryId = serverContext.getSystemsAdminServer().getInventoryManager().getChestInventoryId(motherChest);
        InventoryComponent chestInventory = serverContext.getSystemsAdminServer().getInventoryManager().getChestInventory(motherChest);

        // 0, 1, 2
        // 3, 4, 5
        // 6, 7, 8
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[5], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[8], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[1], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[4], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));

        List<CraftRecipeEntry> craftingTableRecipes = (List<CraftRecipeEntry>) RegistryUtils.findEntries(serverContext.getRootRegistry(), "#craftingTableRecipes");
        List<List<ItemEntry>> mapToItems = serverContext.getSystemsAdminServer().getInventoryManager().mapInventoryToItemRegistry(chestInventoryId);
        Optional<CraftResult> newCraftResult = serverContext.getSystemsAdminServer().getCraftingManager().getNewCraftResult(craftingTableRecipes, mapToItems);

        assertFalse(newCraftResult.isPresent());
    }

    @Test
    public void craftPatternFill() {
        int motherChest = serverContext.getEcsEngineServer().getWorld().create();
        CopperIngotItemEntry copperIngot = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), CopperIngotItemEntry.class);
        EnergyCableItemEntry energyCable = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), EnergyCableItemEntry.class);
        EnergyBatteryItemEntry energyBattery = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), EnergyBatteryItemEntry.class);
        serverContext.getSystemsAdminServer().getInventoryManager().createChest(motherChest, UUID.randomUUID(), 3, 3);
        int chestInventoryId = serverContext.getSystemsAdminServer().getInventoryManager().getChestInventoryId(motherChest);
        InventoryComponent chestInventory = serverContext.getSystemsAdminServer().getInventoryManager().getChestInventory(motherChest);

        // 0, 1, 2
        // 3, 4, 5
        // 6, 7, 8
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[0], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(copperIngot, UUID.randomUUID()));
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[1], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(energyCable, UUID.randomUUID()));
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[2], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(copperIngot, UUID.randomUUID()));
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[3], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(copperIngot, UUID.randomUUID()));
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[4], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(energyCable, UUID.randomUUID()));
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[5], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(copperIngot, UUID.randomUUID()));
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[6], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(copperIngot, UUID.randomUUID()));
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[7], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(energyCable, UUID.randomUUID()));
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[8], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(copperIngot, UUID.randomUUID()));

        EnergyBatteryCraftEntry energyBatteryCraftEntry = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), EnergyBatteryCraftEntry.class);
        List<List<ItemEntry>> mapToItems = serverContext.getSystemsAdminServer().getInventoryManager().mapInventoryToItemRegistry(chestInventoryId);
        Optional<CraftResult> newCraftResult = serverContext.getSystemsAdminServer().getCraftingManager().getNewCraftResult(List.of(energyBatteryCraftEntry), mapToItems);

        assertSame(newCraftResult.orElseThrow().getItemResult(), energyBattery);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void craftCraftingTable() {
        int motherChest = serverContext.getEcsEngineServer().getWorld().create();
        ItemEntry plankItem = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), PlankItemEntry.class);
        ItemEntry craftingTable = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), CraftingTableItemEntry.class);
        serverContext.getSystemsAdminServer().getInventoryManager().createChest(motherChest, UUID.randomUUID(), 2, 2);
        int chestInventoryId = serverContext.getSystemsAdminServer().getInventoryManager().getChestInventoryId(motherChest);
        InventoryComponent chestInventory = serverContext.getSystemsAdminServer().getInventoryManager().getChestInventory(motherChest);

        // 0, 1
        // 2, 3
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[0], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[1], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[2], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[3], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));

        List<CraftRecipeEntry> craftingTableRecipes = (List<CraftRecipeEntry>) RegistryUtils.findEntries(serverContext.getRootRegistry(), "#craftingTableRecipes");
        List<List<ItemEntry>> mapToItems = serverContext.getSystemsAdminServer().getInventoryManager().mapInventoryToItemRegistry(chestInventoryId);
        Optional<CraftResult> newCraftResult = serverContext.getSystemsAdminServer().getCraftingManager().getNewCraftResult(craftingTableRecipes, mapToItems);

        assertEquals(newCraftResult.get().getItemResult(), craftingTable);
    }

    @Test
    public void getCraftResultShapelessTrue() {
        int motherChest = serverContext.getEcsEngineServer().getWorld().create();
        WoodItemEntry woodItem = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), WoodItemEntry.class);
        serverContext.getSystemsAdminServer().getInventoryManager().createChest(motherChest, UUID.randomUUID(), 3, 3);
        int chestInventoryId = serverContext.getSystemsAdminServer().getInventoryManager().getChestInventoryId(motherChest);
        InventoryComponent chestInventory = serverContext.getSystemsAdminServer().getInventoryManager().getChestInventory(motherChest);

        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[0], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(woodItem, UUID.randomUUID()));

        PlankCraftEntry plankCraftEntry = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), PlankCraftEntry.class);
        PlankItemEntry plankItem = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), PlankItemEntry.class);
        List<List<ItemEntry>> mapToItems = serverContext.getSystemsAdminServer().getInventoryManager().mapInventoryToItemRegistry(chestInventoryId);
        Optional<CraftResult> newCraftResult = serverContext.getSystemsAdminServer().getCraftingManager().getNewCraftResult(List.of(plankCraftEntry), mapToItems);

        assertEquals(plankItem, newCraftResult.orElseThrow().getItemResult());
    }

    @Test
    public void getCraftResultShapelessFalse() {
        int motherChest = serverContext.getEcsEngineServer().getWorld().create();
        WoodItemEntry woodItem = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), WoodItemEntry.class);
        serverContext.getSystemsAdminServer().getInventoryManager().createChest(motherChest, UUID.randomUUID(), 3, 3);
        int chestInventoryId = serverContext.getSystemsAdminServer().getInventoryManager().getChestInventoryId(motherChest);
        InventoryComponent chestInventory = serverContext.getSystemsAdminServer().getInventoryManager().getChestInventory(motherChest);

        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[0], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(woodItem, UUID.randomUUID()));
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[1], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(woodItem, UUID.randomUUID()));

        PlankCraftEntry plankCraftEntry = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), PlankCraftEntry.class);
        List<List<ItemEntry>> mapToItems = serverContext.getSystemsAdminServer().getInventoryManager().mapInventoryToItemRegistry(chestInventoryId);
        Optional<CraftResult> newCraftResult = serverContext.getSystemsAdminServer().getCraftingManager().getNewCraftResult(List.of(plankCraftEntry), mapToItems);

        assertTrue(newCraftResult.isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getCraftResultChestInvalide() {
        int motherChest = serverContext.getEcsEngineServer().getWorld().create();
        ItemEntry plankItem = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), PlankItemEntry.class);
        serverContext.getSystemsAdminServer().getInventoryManager().createChest(motherChest, UUID.randomUUID(), 3, 3);
        int chestInventoryId = serverContext.getSystemsAdminServer().getInventoryManager().getChestInventoryId(motherChest);
        InventoryComponent chestInventory = serverContext.getSystemsAdminServer().getInventoryManager().getChestInventory(motherChest);

        // 0, 1, 2
        // 3, 4, 5
        // 6, 7, 8
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[0], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[1], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[2], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[3], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[4], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[5], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[6], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[7], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[8], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));

        List<CraftRecipeEntry> craftingTableRecipes = (List<CraftRecipeEntry>) RegistryUtils.findEntries(serverContext.getRootRegistry(), "#craftingTableRecipes");
        List<List<ItemEntry>> mapToItems = serverContext.getSystemsAdminServer().getInventoryManager().mapInventoryToItemRegistry(chestInventoryId);
        Optional<CraftResult> newCraftResult = serverContext.getSystemsAdminServer().getCraftingManager().getNewCraftResult(craftingTableRecipes, mapToItems);

        assertFalse(newCraftResult.isPresent());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getCraftResultFurnace() {
        int motherChest = serverContext.getEcsEngineServer().getWorld().create();
        ItemEntry copperOre = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), CopperOreItemEntry.class);
        serverContext.getSystemsAdminServer().getInventoryManager().createChest(motherChest, UUID.randomUUID(), 1, 1);
        int chestInventoryId = serverContext.getSystemsAdminServer().getInventoryManager().getChestInventoryId(motherChest);
        InventoryComponent chestInventory = serverContext.getSystemsAdminServer().getInventoryManager().getChestInventory(motherChest);

        // 0
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[0], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(copperOre, UUID.randomUUID()));

        List<FurnaceCraftShapeless> furnaceRecipes = (List<FurnaceCraftShapeless>) RegistryUtils.findEntries(serverContext.getRootRegistry(), "#furnaceRecipes");
        List<List<ItemEntry>> mapToItems = serverContext.getSystemsAdminServer().getInventoryManager().mapInventoryToItemRegistry(chestInventoryId);
        Optional<CraftResult> newCraftResult = serverContext.getSystemsAdminServer().getCraftingManager().getNewCraftResult(furnaceRecipes, mapToItems);

        assertTrue(newCraftResult.isPresent());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getCraftResultFurnaceFalse() {
        int motherChest = serverContext.getEcsEngineServer().getWorld().create();
        ItemEntry copperOre = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), CopperOreItemEntry.class);
        serverContext.getSystemsAdminServer().getInventoryManager().createChest(motherChest, UUID.randomUUID(), 2, 1);
        int chestInventoryId = serverContext.getSystemsAdminServer().getInventoryManager().getChestInventoryId(motherChest);
        InventoryComponent chestInventory = serverContext.getSystemsAdminServer().getInventoryManager().getChestInventory(motherChest);

        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[0], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(copperOre, UUID.randomUUID()));
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[1], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(copperOre, UUID.randomUUID()));

        List<FurnaceCraftShapeless> furnaceRecipes = (List<FurnaceCraftShapeless>) RegistryUtils.findEntries(serverContext.getRootRegistry(), "#furnaceRecipes");
        List<List<ItemEntry>> mapToItems = serverContext.getSystemsAdminServer().getInventoryManager().mapInventoryToItemRegistry(chestInventoryId);
        Optional<CraftResult> newCraftResult = serverContext.getSystemsAdminServer().getCraftingManager().getNewCraftResult(furnaceRecipes, mapToItems);

        assertFalse(newCraftResult.isPresent());
    }

    @Test
    public void testInvalideRecipe() {
        int motherChest = serverContext.getEcsEngineServer().getWorld().create();
        ItemEntry plankItem = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), PlankItemEntry.class);
        serverContext.getSystemsAdminServer().getInventoryManager().createChest(motherChest, UUID.randomUUID(), 3, 3);
        int chestInventoryId = serverContext.getSystemsAdminServer().getInventoryManager().getChestInventoryId(motherChest);
        InventoryComponent chestInventory = serverContext.getSystemsAdminServer().getInventoryManager().getChestInventory(motherChest);

        // 0, 1, 2      x, 0, 0
        // 3, 4, 5      0, 0, x
        // 6, 7, 8      0, x, x
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[0], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[5], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[7], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdminServer().getInventoryManager().addItemToStack(chestInventory.inventory[8], serverContext.getSystemsAdminServer().getItemManagerServer().newItemInventory(plankItem, UUID.randomUUID()));

        CraftingTableCraftEntry craftEntry = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), CraftingTableCraftEntry.class);
        List<CraftRecipeEntry> craftingTableRecipes = List.of(craftEntry);
        List<List<ItemEntry>> mapToItems = serverContext.getSystemsAdminServer().getInventoryManager().mapInventoryToItemRegistry(chestInventoryId);
        Optional<CraftResult> newCraftResult = serverContext.getSystemsAdminServer().getCraftingManager().getNewCraftResult(craftingTableRecipes, mapToItems);

        assertFalse(newCraftResult.isPresent());
    }
}
