package ch.realmtech.server.newMod.CraftEntry;

import ch.realmtech.server.newCraft.NewPatternArgs;
import ch.realmtech.server.newRegistry.NewCraftPatternShape;

public class WrenchCraftEntry extends NewCraftPatternShape {
    public WrenchCraftEntry() {
        super("WrenchCraft", "realmtech.items.Wrench", 1, new char[][]{
                {'a', ' ', 'a'},
                {' ', 'a', ' '},
                {' ', 'a', ' '},
        }, new NewPatternArgs('a', "realmtech.items.IronIngot"));
    }
}
