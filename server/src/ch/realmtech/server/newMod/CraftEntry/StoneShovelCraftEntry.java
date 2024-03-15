package ch.realmtech.server.newMod.CraftEntry;

import ch.realmtech.server.newCraft.NewPatternArgs;
import ch.realmtech.server.newRegistry.NewCraftPatternShape;

public class StoneShovelCraftEntry extends NewCraftPatternShape {
    public StoneShovelCraftEntry() {
        super("StoneShovelCraft", "realmtech.items.StoneShovel", 1, new char[][]{
                {' ', 's', ' '},
                {' ', 'a', ' '},
                {' ', 'a', ' '},
        }, new NewPatternArgs('s', "realmtech.items.StoneOre"), new NewPatternArgs('a', "realmtech.items.Stick"));
    }
}
