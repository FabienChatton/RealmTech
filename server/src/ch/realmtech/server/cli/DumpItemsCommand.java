package ch.realmtech.server.cli;

import ch.realmtech.server.ecs.component.ItemComponent;
import ch.realmtech.server.item.ItemInfoHelper;
import com.artemis.Aspect;
import com.artemis.utils.IntBag;

import java.util.concurrent.Callable;

import static picocli.CommandLine.*;

@Command(name = "items", description = "dump all items present in the world")
public class DumpItemsCommand implements Callable<Integer> {
    @ParentCommand
    private DumpCommand dumpCommand;

    @Option(names = {"-v", "--verbose"}, description = "Show more detail about items result")
    private boolean verbose;
    @Override
    public Integer call() throws Exception {
        IntBag itemsEntities = dumpCommand.masterCommand.getWorld().getAspectSubscriptionManager().get(Aspect.all(
                ItemComponent.class
        )).getEntities();
        int[] itemsData = itemsEntities.getData();
        if (verbose) {
            for (int i = 0; i < itemsEntities.size(); i++) {
                int itemId = itemsData[i];
                dumpCommand.masterCommand.output.println(ItemInfoHelper.dumpItem(dumpCommand.masterCommand.getWorld(), itemId));
            }
        }
        if (itemsEntities.size() == 0) dumpCommand.masterCommand.output.println("no items loaded");
        else dumpCommand.masterCommand.output.println("Items count: " + itemsEntities.size());
        return 0;
    }
}
