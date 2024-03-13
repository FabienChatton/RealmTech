package ch.realmtech.server.newMod.CraftEntry;

import ch.realmtech.server.newCraft.NewPatternArgs;
import ch.realmtech.server.newRegistry.InvalideEvaluate;
import ch.realmtech.server.newRegistry.NewCraftPatternShape;
import ch.realmtech.server.newRegistry.NewRegistry;

public class ChestCraftEntry extends NewCraftPatternShape {
    public ChestCraftEntry() {
        super("chestCraft", "realmtech.items.chest", 1, new char[][]{
                {'p', 'p', 'p'},
                {'p', ' ', 'p'},
                {'p', 'p', 'p'}
        }, new NewPatternArgs('p', "realmtech.items.plank"));
    }

    @Override
    public void evaluate(NewRegistry<?> rootRegistry) throws InvalideEvaluate {
        super.evaluate(rootRegistry);
    }
}
