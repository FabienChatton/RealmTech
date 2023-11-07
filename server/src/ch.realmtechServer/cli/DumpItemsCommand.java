package ch.realmtechServer.cli;

import ch.realmtechServer.ecs.component.ItemComponent;
import ch.realmtechServer.item.ItemInfoHelper;
import com.artemis.Aspect;
import com.artemis.utils.IntBag;

import java.util.concurrent.Callable;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.ParentCommand;

@Command(name = "items", description = "dump all items present in the world")
public class DumpItemsCommand implements Callable<Integer> {
    @ParentCommand
    private DumpCommand dumpCommand;
    @Override
    public Integer call() throws Exception {
        IntBag itemsEntities = dumpCommand.masterCommand.getWorld().getAspectSubscriptionManager().get(Aspect.all(
                ItemComponent.class
        )).getEntities();
        int[] data = itemsEntities.getData();
        for (int i = 0; i < itemsEntities.size(); i++) {
            dumpCommand.masterCommand.output.println(ItemInfoHelper.dumpItem(dumpCommand.masterCommand.getWorld(), data[i]));
        }
        if (itemsEntities.size() == 0) dumpCommand.masterCommand.output.println("no items loaded");
        return 0;
    }
}
