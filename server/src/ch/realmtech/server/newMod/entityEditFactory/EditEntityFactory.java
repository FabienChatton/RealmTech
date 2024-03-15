package ch.realmtech.server.newMod.entityEditFactory;

import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.level.cell.CraftingTableEditEntity;
import ch.realmtech.server.newMod.EvaluateAfter;
import ch.realmtech.server.newRegistry.*;
import ch.realmtech.server.uuid.UuidSupplierOrRandom;

import java.util.List;
import java.util.UUID;

public class EditEntityFactory extends NewEntry {
    private List<NewCraftRecipeEntry> craftRecipes;

    public EditEntityFactory() {
        super("factory");
    }

    @Override
    @SuppressWarnings("unchecked")
    @EvaluateAfter("realmtech.crafts.craftingTable")
    public void evaluate(NewRegistry<?> rootRegistry) throws InvalideEvaluate {
        String tagQuery = "#craftingTableRecipes";
        List<? extends NewEntry> craftRecipeEntires = RegistryUtils.findEntries(rootRegistry, tagQuery);
        craftRecipes = (List<NewCraftRecipeEntry>) craftRecipeEntires;
    }

    public CraftingTableEditEntity createCraftingTable(int craftingNumberOfSlotParRow, int craftingNumberOfRow) {
        return new CraftingTableEditEntity(new UuidSupplierOrRandom(), new int[craftingNumberOfSlotParRow * craftingNumberOfRow][InventoryComponent.DEFAULT_STACK_LIMITE], craftingNumberOfSlotParRow, craftingNumberOfRow, new UuidSupplierOrRandom(), craftRecipes);
    }

    public CraftingTableEditEntity createSetCraftingTable(UUID craftingInventoryUuid, int[][] inventory, int craftingNumberOfSlotParRow, int craftingNumberOfRow, UUID craftingResultInventoryUuid) {
        return new CraftingTableEditEntity(new UuidSupplierOrRandom(craftingInventoryUuid), inventory, craftingNumberOfSlotParRow, craftingNumberOfRow, new UuidSupplierOrRandom(craftingResultInventoryUuid), craftRecipes);
    }
}
