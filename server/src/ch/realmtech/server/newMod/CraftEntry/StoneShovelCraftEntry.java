package ch.realmtech.server.newMod.CraftEntry;

import ch.realmtech.server.newCraft.NewPatternArgs;
import ch.realmtech.server.newRegistry.NewCraftPatternShape;

public class StoneShovelCraftEntry extends NewCraftPatternShape {
    public StoneShovelCraftEntry() {
        super("stoneShovelCraft", "realmtech.items.stoneShovel", 1, new char[][]{
                {' ', 's', ' '},
                {' ', 'a', ' '},
                {' ', 'a', ' '},
        }, new NewPatternArgs('s', "realmtech.items.stoneOre"), new NewPatternArgs('a', "realmtech.items.stick"));
    }
}
