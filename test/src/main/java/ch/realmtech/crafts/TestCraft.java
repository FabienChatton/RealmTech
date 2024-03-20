package ch.realmtech.crafts;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.craft.CraftResult;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.mod.items.CopperOreItemEntry;
import ch.realmtech.server.mod.items.PlankItemEntry;
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
        serverContext.getSystemsAdmin().inventoryManager.createChest(motherChest, UUID.randomUUID(), 3, 3);
        int chestInventoryId = serverContext.getSystemsAdmin().inventoryManager.getChestInventoryId(motherChest);
        InventoryComponent chestInventory = serverContext.getSystemsAdmin().inventoryManager.getChestInventory(motherChest);

        // 0, 1, 2
        // 3, 4, 5
        // 6, 7, 8
        serverContext.getSystemsAdmin().inventoryManager.addItemToStack(chestInventory.inventory[0], serverContext.getSystemsAdmin().itemManagerServer.newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdmin().inventoryManager.addItemToStack(chestInventory.inventory[1], serverContext.getSystemsAdmin().itemManagerServer.newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdmin().inventoryManager.addItemToStack(chestInventory.inventory[2], serverContext.getSystemsAdmin().itemManagerServer.newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdmin().inventoryManager.addItemToStack(chestInventory.inventory[3], serverContext.getSystemsAdmin().itemManagerServer.newItemInventory(plankItem, UUID.randomUUID()));
        // empty plank in center for chest craft
        serverContext.getSystemsAdmin().inventoryManager.addItemToStack(chestInventory.inventory[5], serverContext.getSystemsAdmin().itemManagerServer.newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdmin().inventoryManager.addItemToStack(chestInventory.inventory[6], serverContext.getSystemsAdmin().itemManagerServer.newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdmin().inventoryManager.addItemToStack(chestInventory.inventory[7], serverContext.getSystemsAdmin().itemManagerServer.newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdmin().inventoryManager.addItemToStack(chestInventory.inventory[8], serverContext.getSystemsAdmin().itemManagerServer.newItemInventory(plankItem, UUID.randomUUID()));

        List<List<ItemEntry>> mapToItems = serverContext.getSystemsAdmin().inventoryManager.mapInventoryToItemRegistry(chestInventoryId);
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
    public void getCraftResultChest() {
        int motherChest = serverContext.getEcsEngineServer().getWorld().create();
        ItemEntry plankItem = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), PlankItemEntry.class);
        serverContext.getSystemsAdmin().inventoryManager.createChest(motherChest, UUID.randomUUID(), 3, 3);
        int chestInventoryId = serverContext.getSystemsAdmin().inventoryManager.getChestInventoryId(motherChest);
        InventoryComponent chestInventory = serverContext.getSystemsAdmin().inventoryManager.getChestInventory(motherChest);

        // 0, 1, 2
        // 3, 4, 5
        // 6, 7, 8
        serverContext.getSystemsAdmin().inventoryManager.addItemToStack(chestInventory.inventory[0], serverContext.getSystemsAdmin().itemManagerServer.newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdmin().inventoryManager.addItemToStack(chestInventory.inventory[1], serverContext.getSystemsAdmin().itemManagerServer.newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdmin().inventoryManager.addItemToStack(chestInventory.inventory[2], serverContext.getSystemsAdmin().itemManagerServer.newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdmin().inventoryManager.addItemToStack(chestInventory.inventory[3], serverContext.getSystemsAdmin().itemManagerServer.newItemInventory(plankItem, UUID.randomUUID()));
        // empty plank in center for chest craft
        serverContext.getSystemsAdmin().inventoryManager.addItemToStack(chestInventory.inventory[5], serverContext.getSystemsAdmin().itemManagerServer.newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdmin().inventoryManager.addItemToStack(chestInventory.inventory[6], serverContext.getSystemsAdmin().itemManagerServer.newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdmin().inventoryManager.addItemToStack(chestInventory.inventory[7], serverContext.getSystemsAdmin().itemManagerServer.newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdmin().inventoryManager.addItemToStack(chestInventory.inventory[8], serverContext.getSystemsAdmin().itemManagerServer.newItemInventory(plankItem, UUID.randomUUID()));

        List<CraftRecipeEntry> craftingTableRecipes = (List<CraftRecipeEntry>) RegistryUtils.findEntries(serverContext.getRootRegistry(), "#craftingTableRecipes");
        List<List<ItemEntry>> mapToItems = serverContext.getSystemsAdmin().inventoryManager.mapInventoryToItemRegistry(chestInventoryId);
        Optional<CraftResult> newCraftResult = serverContext.getSystemsAdmin().craftingManager.getNewCraftResult(craftingTableRecipes, mapToItems);

        assertTrue(newCraftResult.isPresent());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getCraftResultChestInvalide() {
        int motherChest = serverContext.getEcsEngineServer().getWorld().create();
        ItemEntry plankItem = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), PlankItemEntry.class);
        serverContext.getSystemsAdmin().inventoryManager.createChest(motherChest, UUID.randomUUID(), 3, 3);
        int chestInventoryId = serverContext.getSystemsAdmin().inventoryManager.getChestInventoryId(motherChest);
        InventoryComponent chestInventory = serverContext.getSystemsAdmin().inventoryManager.getChestInventory(motherChest);

        // 0, 1, 2
        // 3, 4, 5
        // 6, 7, 8
        serverContext.getSystemsAdmin().inventoryManager.addItemToStack(chestInventory.inventory[0], serverContext.getSystemsAdmin().itemManagerServer.newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdmin().inventoryManager.addItemToStack(chestInventory.inventory[1], serverContext.getSystemsAdmin().itemManagerServer.newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdmin().inventoryManager.addItemToStack(chestInventory.inventory[2], serverContext.getSystemsAdmin().itemManagerServer.newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdmin().inventoryManager.addItemToStack(chestInventory.inventory[3], serverContext.getSystemsAdmin().itemManagerServer.newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdmin().inventoryManager.addItemToStack(chestInventory.inventory[4], serverContext.getSystemsAdmin().itemManagerServer.newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdmin().inventoryManager.addItemToStack(chestInventory.inventory[5], serverContext.getSystemsAdmin().itemManagerServer.newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdmin().inventoryManager.addItemToStack(chestInventory.inventory[6], serverContext.getSystemsAdmin().itemManagerServer.newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdmin().inventoryManager.addItemToStack(chestInventory.inventory[7], serverContext.getSystemsAdmin().itemManagerServer.newItemInventory(plankItem, UUID.randomUUID()));
        serverContext.getSystemsAdmin().inventoryManager.addItemToStack(chestInventory.inventory[8], serverContext.getSystemsAdmin().itemManagerServer.newItemInventory(plankItem, UUID.randomUUID()));

        List<CraftRecipeEntry> craftingTableRecipes = (List<CraftRecipeEntry>) RegistryUtils.findEntries(serverContext.getRootRegistry(), "#craftingTableRecipes");
        List<List<ItemEntry>> mapToItems = serverContext.getSystemsAdmin().inventoryManager.mapInventoryToItemRegistry(chestInventoryId);
        Optional<CraftResult> newCraftResult = serverContext.getSystemsAdmin().craftingManager.getNewCraftResult(craftingTableRecipes, mapToItems);

        assertFalse(newCraftResult.isPresent());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getCraftResultFurnace() {
        int motherChest = serverContext.getEcsEngineServer().getWorld().create();
        ItemEntry copperOre = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), CopperOreItemEntry.class);
        serverContext.getSystemsAdmin().inventoryManager.createChest(motherChest, UUID.randomUUID(), 1, 1);
        int chestInventoryId = serverContext.getSystemsAdmin().inventoryManager.getChestInventoryId(motherChest);
        InventoryComponent chestInventory = serverContext.getSystemsAdmin().inventoryManager.getChestInventory(motherChest);

        // 0
        serverContext.getSystemsAdmin().inventoryManager.addItemToStack(chestInventory.inventory[0], serverContext.getSystemsAdmin().itemManagerServer.newItemInventory(copperOre, UUID.randomUUID()));

        List<FurnaceCraftShapeless> furnaceRecipes = (List<FurnaceCraftShapeless>) RegistryUtils.findEntries(serverContext.getRootRegistry(), "#furnaceRecipes");
        List<List<ItemEntry>> mapToItems = serverContext.getSystemsAdmin().inventoryManager.mapInventoryToItemRegistry(chestInventoryId);
        Optional<CraftResult> newCraftResult = serverContext.getSystemsAdmin().craftingManager.getNewCraftResult(furnaceRecipes, mapToItems);

        assertTrue(newCraftResult.isPresent());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getCraftResultFurnaceFalse() {
        int motherChest = serverContext.getEcsEngineServer().getWorld().create();
        ItemEntry copperOre = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), CopperOreItemEntry.class);
        serverContext.getSystemsAdmin().inventoryManager.createChest(motherChest, UUID.randomUUID(), 2, 1);
        int chestInventoryId = serverContext.getSystemsAdmin().inventoryManager.getChestInventoryId(motherChest);
        InventoryComponent chestInventory = serverContext.getSystemsAdmin().inventoryManager.getChestInventory(motherChest);

        serverContext.getSystemsAdmin().inventoryManager.addItemToStack(chestInventory.inventory[0], serverContext.getSystemsAdmin().itemManagerServer.newItemInventory(copperOre, UUID.randomUUID()));
        serverContext.getSystemsAdmin().inventoryManager.addItemToStack(chestInventory.inventory[1], serverContext.getSystemsAdmin().itemManagerServer.newItemInventory(copperOre, UUID.randomUUID()));

        List<FurnaceCraftShapeless> furnaceRecipes = (List<FurnaceCraftShapeless>) RegistryUtils.findEntries(serverContext.getRootRegistry(), "#furnaceRecipes");
        List<List<ItemEntry>> mapToItems = serverContext.getSystemsAdmin().inventoryManager.mapInventoryToItemRegistry(chestInventoryId);
        Optional<CraftResult> newCraftResult = serverContext.getSystemsAdmin().craftingManager.getNewCraftResult(furnaceRecipes, mapToItems);

        assertFalse(newCraftResult.isPresent());
    }
}
