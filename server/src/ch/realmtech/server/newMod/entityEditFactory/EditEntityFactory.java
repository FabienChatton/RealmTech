package ch.realmtech.server.newMod.entityEditFactory;

import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.level.cell.CraftingTableEditEntity;
import ch.realmtech.server.newMod.EvaluateAfter;
import ch.realmtech.server.newRegistry.*;
import ch.realmtech.server.uuid.UuidSupplierOrRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EditEntityFactory extends NewEntry {
    public final static String KNOW_FQRN = "realmtech.editEntity.factory";
    private List<NewCraftRecipeEntry> craftRecipes;

    public EditEntityFactory() {
        super("factory");
    }

    @Override
    @EvaluateAfter("realmtech.crafts.craftingTable")
    public void evaluate(NewRegistry<?> rootRegistry) throws InvalideEvaluate {
        super.evaluate(rootRegistry);
        String tagQuery = "#craftingTableRecipes";
        Optional<NewRegistry<?>> craftRegistry = RegistryUtils.findRegistry(rootRegistry, tagQuery);
        if (craftRegistry.isEmpty()) {
            throw new InvalideEvaluate("Can not find " + tagQuery + " entry.");
        } else {
            NewRegistry<?> registry = craftRegistry.get();
            craftRecipes = (List<NewCraftRecipeEntry>) new ArrayList<>(registry.getEntries());
        }
    }

    public CraftingTableEditEntity createCraftingTable(int craftingNumberOfSlotParRow, int craftingNumberOfRow) {
        return new CraftingTableEditEntity(new UuidSupplierOrRandom(), new int[craftingNumberOfSlotParRow * craftingNumberOfRow][InventoryComponent.DEFAULT_STACK_LIMITE], craftingNumberOfSlotParRow, craftingNumberOfRow, new UuidSupplierOrRandom(), craftRecipes);
    }

    public CraftingTableEditEntity createSetCraftingTable(UUID craftingInventoryUuid, int[][] inventory, int craftingNumberOfSlotParRow, int craftingNumberOfRow, UUID craftingResultInventoryUuid) {
        return new CraftingTableEditEntity(new UuidSupplierOrRandom(craftingInventoryUuid), inventory, craftingNumberOfSlotParRow, craftingNumberOfRow, new UuidSupplierOrRandom(craftingResultInventoryUuid), craftRecipes);
    }
}
