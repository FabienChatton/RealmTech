package ch.realmtechServer.cli;

import ch.realmtechServer.ecs.component.ItemComponent;
import ch.realmtechServer.ecs.component.ItemResultCraftComponent;
import ch.realmtechServer.item.ItemInfoHelper;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.utils.IntBag;

import java.util.concurrent.Callable;

import static picocli.CommandLine.*;

@Command(name="items-craft-results", aliases = "itemsc", description = "Dump craft items result")
public class DumpCraftResultCommand implements Callable<Integer> {
    @ParentCommand
    private DumpCommand dumpCommand;

    @Option(names = {"-v", "--verbose"}, description = "Show more detail about items result")
    private boolean verbose;

    @Override
    public Integer call() throws Exception {
        IntBag itemsResultEntities = dumpCommand.masterCommand.getWorld().getAspectSubscriptionManager().get(Aspect.all(ItemResultCraftComponent.class)).getEntities();
        ComponentMapper<ItemComponent> mItems = dumpCommand.masterCommand.getWorld().getMapper(ItemComponent.class);
        ComponentMapper<ItemResultCraftComponent> mItemResult = dumpCommand.masterCommand.getWorld().getMapper(ItemResultCraftComponent.class);
        int[] itemsResultData = itemsResultEntities.getData();

        if (verbose) {
            for (int i = 0; i < itemsResultEntities.size(); i++) {
                int itemResult = itemsResultData[i];
                ItemResultCraftComponent itemResultCraftComponent = mItemResult.get(itemResult);
                ItemComponent itemComponent = mItems.get(itemResult);
                ItemInfoHelper.dumpItem(dumpCommand.masterCommand.getWorld(), itemResult);
            }
        }

        if (itemsResultEntities.size() == 0) dumpCommand.masterCommand.output.println("no items craft result loaded");
        else dumpCommand.masterCommand.output.println("Chunk count : " + itemsResultEntities.size());
        return 0;
    }
}
