package ch.realmtech.server.mod.commandes.dump;

import ch.realmtech.server.ecs.component.ItemComponent;
import ch.realmtech.server.item.ItemInfoHelper;
import ch.realmtech.server.registry.CommandEntry;
import com.artemis.Aspect;
import com.artemis.utils.IntBag;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.ParentCommand;

@Command(name = "items", description = "dump all items present in the world")
public class DumpItemsCommandEntry extends CommandEntry {
    public DumpItemsCommandEntry() {
        super("DumpItems");
    }

    @ParentCommand
    public DumpCommandEntry dumpCommand;

    @Override
    public void run() {
        IntBag itemsEntities = dumpCommand.masterCommand.getWorld().getAspectSubscriptionManager().get(Aspect.all(
                ItemComponent.class
        )).getEntities();
        int[] itemsData = itemsEntities.getData();
        for (int i = 0; i < itemsEntities.size(); i++) {
            int itemId = itemsData[i];
            dumpCommand.printlnVerbose(1, ItemInfoHelper.dumpItem(dumpCommand.masterCommand.getWorld(), itemId));
        }
        if (itemsEntities.isEmpty()) dumpCommand.masterCommand.output.println("no items loaded");
        else dumpCommand.masterCommand.output.println("Items count: " + itemsEntities.size());
    }
}
