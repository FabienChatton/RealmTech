package ch.realmtech.server.cli;


import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.component.ItemComponent;
import ch.realmtech.server.ecs.component.UuidComponent;
import ch.realmtech.server.ecs.system.InventoryManager;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.utils.IntBag;

import java.util.Arrays;
import java.util.concurrent.Callable;

import static picocli.CommandLine.*;

@Command(name="inventory", description = "dump all inventories")
public class DumpInventoryCommand implements Callable<Integer> {
    @ParentCommand
    private DumpCommand dumpCommand;

    @Option(names = {"-v", "--verbose"}, description = "Show more detail about inventory")
    private boolean[] verbose;

    @Override
    public Integer call() throws Exception {
        IntBag inventoryEntities = dumpCommand.masterCommand.getWorld().getAspectSubscriptionManager().get(Aspect.all(InventoryComponent.class, UuidComponent.class)).getEntities();
        int[] inventoryData = inventoryEntities.getData();
        ComponentMapper<InventoryComponent> mInventory = dumpCommand.masterCommand.getWorld().getMapper(InventoryComponent.class);
        ComponentMapper<UuidComponent> mUuid = dumpCommand.masterCommand.getWorld().getMapper(UuidComponent.class);
        ComponentMapper<ItemComponent> mItem = dumpCommand.masterCommand.getWorld().getMapper(ItemComponent.class);
        if (verbose != null && verbose.length >= 1) {
            for (int i = 0; i < inventoryEntities.size(); i++) {
                int inventoryId = inventoryData[i];
                UuidComponent uuidComponent = mUuid.get(inventoryId);
                InventoryComponent inventoryComponent = mInventory.get(inventoryId);
                dumpCommand.masterCommand.output.println(String.format("%s, %s", uuidComponent, inventoryComponent));
                if (verbose.length >= 2) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int s = 0; s < inventoryComponent.inventory.length; s++) {
                        ItemComponent itemComponent = mItem.get(inventoryComponent.inventory[s][0]);
                        if (itemComponent != null) {
                            stringBuilder.append(String.format("%s %d", itemComponent.itemRegisterEntry, InventoryManager.tailleStack(inventoryComponent.inventory[s])));
                            stringBuilder.append(" ");
                        }
                    }
                    if (!stringBuilder.toString().isBlank()) {
                        dumpCommand.masterCommand.output.println(stringBuilder);
                    }
                }
                if (verbose.length >= 3) {
                    byte[] inventoryBytes = dumpCommand.masterCommand.getSerializerManagerController().getInventorySerializerManager().toBytesLatest(dumpCommand.masterCommand.getWorld(), dumpCommand.masterCommand.getSerializerManagerController(), inventoryComponent);
                    dumpCommand.masterCommand.output.println(String.format("%s", Arrays.toString(inventoryBytes)));
                }
            }
        }
        if (inventoryEntities.size() == 0) dumpCommand.masterCommand.output.println("no inventories loaded");
        else dumpCommand.masterCommand.output.println("inventories count: " + inventoryEntities.size());
        return 0;
    }
}
