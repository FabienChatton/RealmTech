package ch.realmtech.server.newMod.CraftEntry;

import ch.realmtech.server.newCraft.NewPatternArgs;
import ch.realmtech.server.newRegistry.NewCraftPatternShape;

public class EnergyCableCraftEntry extends NewCraftPatternShape {
    public EnergyCableCraftEntry() {
        super("EnergyCableCraft", "realmtech.items.EnergyCable", 1, new char[][]{
                {'c', 'c', 'c'}
        }, new NewPatternArgs('c', "realmtech.items.CopperIngot"));
    }
}
