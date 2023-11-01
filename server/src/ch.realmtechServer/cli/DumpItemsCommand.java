package ch.realmtechServer.cli;

import ch.realmtechServer.ecs.component.ItemComponent;
import ch.realmtechServer.item.ItemInfoHelper;
import com.artemis.Aspect;
import com.artemis.utils.IntBag;

import java.util.concurrent.Callable;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.ParentCommand;

@Command(name = "dumpItems", description = "affiche tous les items present dans le monde", mixinStandardHelpOptions = true)
public class DumpItemsCommand implements Callable<Integer> {
    @ParentCommand
    private CommunMasterCommand communMasterCommand;
    @Override
    public Integer call() throws Exception {
        IntBag itemsEntities = communMasterCommand.getWorld().getAspectSubscriptionManager().get(Aspect.all(
                ItemComponent.class
        )).getEntities();
        int[] data = itemsEntities.getData();
        for (int i = 0; i < itemsEntities.size(); i++) {
            communMasterCommand.output.println(ItemInfoHelper.dumpItem(communMasterCommand.getWorld(), data[i]));
        }
        if (itemsEntities.size() == 0) communMasterCommand.output.println("no items");
        return 0;
    }
}
