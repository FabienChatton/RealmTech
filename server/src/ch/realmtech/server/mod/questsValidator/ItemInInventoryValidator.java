package ch.realmtech.server.mod.questsValidator;

import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.registry.*;

import java.util.Collection;
import java.util.List;

public class ItemInInventoryValidator implements QuestEntryValidator, Evaluator {

    private final String itemFqrn;
    private ItemEntry itemEntry;

    public ItemInInventoryValidator(String itemFqrn) {
        this.itemFqrn = itemFqrn;
    }

    @Override
    public void evaluate(Registry<?> rootRegistry) throws InvalideEvaluate {
        itemEntry = RegistryUtils.evaluateSafe(rootRegistry, itemFqrn, ItemEntry.class);
    }

    @Override
    public boolean validate(SystemsAdminServer systemsAdminServer, int inventoryId) {
        List<ItemEntry> mapItems = systemsAdminServer.getInventoryManager().mapInventoryToItemRegistry(inventoryId).stream().flatMap(Collection::stream).toList();
        return mapItems.contains(itemEntry);
    }
}
