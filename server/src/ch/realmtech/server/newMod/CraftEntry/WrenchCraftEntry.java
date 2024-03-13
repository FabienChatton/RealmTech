package ch.realmtech.server.newMod.CraftEntry;

import ch.realmtech.server.newCraft.NewPatternArgs;
import ch.realmtech.server.newRegistry.NewCraftPatternShape;

public class WrenchCraftEntry extends NewCraftPatternShape {
    public WrenchCraftEntry() {
        super("wrenchCraft", "realmtech.items.wrench", 1, new char[][]{
                {'a', ' ', 'a'},
                {' ', 'a', ' '},
                {' ', 'a', ' '},
        }, new NewPatternArgs('a', "realmtech.items.ironIngot"));
    }
}
