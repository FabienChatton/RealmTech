package ch.realmtech.server.mod.commandes.dump;

import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.component.ItemComponent;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import ch.realmtech.server.ecs.system.InventoryManager;
import ch.realmtech.server.registry.CommandEntry;
import ch.realmtech.server.serialize.types.SerializedApplicationBytes;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.utils.IntBag;

import java.util.Arrays;
import java.util.UUID;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.ParentCommand;

@Command(name = "inventory", description = "dump all inventories")
public class DumpInventoryCommandEntry extends CommandEntry {
    public DumpInventoryCommandEntry() {
        super("DumpInventory");
    }

    @ParentCommand
    public DumpCommandEntry dumpCommand;

    @Override
    public void run() {
        IntBag inventoryEntities = dumpCommand.masterCommand.getWorld().getAspectSubscriptionManager().get(Aspect.all(InventoryComponent.class)).getEntities();
        int[] inventoryData = inventoryEntities.getData();
        SystemsAdminCommun systemsAdminCommun = dumpCommand.masterCommand.getWorld().getRegistered("systemsAdmin");
        ComponentMapper<InventoryComponent> mInventory = dumpCommand.masterCommand.getWorld().getMapper(InventoryComponent.class);
        ComponentMapper<ItemComponent> mItem = dumpCommand.masterCommand.getWorld().getMapper(ItemComponent.class);
        for (int i = 0; i < inventoryEntities.size(); i++) {
            int inventoryId = inventoryData[i];
            InventoryComponent inventoryComponent = mInventory.get(inventoryId);
            UUID entityUuid = systemsAdminCommun.getUuidEntityManager().getEntityUuid(inventoryId);
            dumpCommand.printlnVerbose(1, String.format("%s, %s", entityUuid, inventoryComponent));
            StringBuilder stringBuilder = new StringBuilder();
            for (int s = 0; s < inventoryComponent.inventory.length; s++) {
                ItemComponent itemComponent = mItem.get(inventoryComponent.inventory[s][0]);
                if (itemComponent != null) {
                    stringBuilder.append(String.format("%s %d", itemComponent.itemRegisterEntry, InventoryManager.tailleStack(inventoryComponent.inventory[s])));
                    stringBuilder.append(" ");
                }
            }
            if (!stringBuilder.toString().isBlank()) {
                dumpCommand.printlnVerbose(2, stringBuilder);
            }
            SerializedApplicationBytes applicationInventoryBytes = dumpCommand.masterCommand.getSerializerController().getInventorySerializerManager().encode(inventoryComponent);
            dumpCommand.printlnVerbose(3, String.format("%s", Arrays.toString(applicationInventoryBytes.applicationBytes())));

        }
        if (inventoryEntities.isEmpty()) dumpCommand.printlnVerbose(0, "no inventories loaded");
        else dumpCommand.printlnVerbose(0, "inventories count: " + inventoryEntities.size());
    }
}
