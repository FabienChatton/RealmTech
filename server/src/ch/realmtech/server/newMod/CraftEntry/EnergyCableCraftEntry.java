package ch.realmtech.server.newMod.CraftEntry;

import ch.realmtech.server.newCraft.NewPatternArgs;
import ch.realmtech.server.newRegistry.NewCraftPatternShape;

public class EnergyCableCraftEntry extends NewCraftPatternShape {
    public EnergyCableCraftEntry() {
        super("energyCableCraft", "realmtech.items.energyCable", 1, new char[][]{
                {'c', 'c', 'c'}
        }, new NewPatternArgs('c', "realmtech.items.copperIngot"));
    }
}
