package ch.realmtech.server.newMod.CraftEntry;

import ch.realmtech.server.newCraft.NewPatternArgs;
import ch.realmtech.server.newRegistry.NewCraftPatternShape;

public class WoodenShovelCraftEntry extends NewCraftPatternShape {
    public WoodenShovelCraftEntry() {
        super("woodenShovelCraft", "realmtech.items.woodenShovel", 1, new char[][]{
                {' ', 'a', ' '},
                {' ', 'b', ' '},
                {' ', 'b', ' '}
        }, new NewPatternArgs('a', "realmtech.items.plank"), new NewPatternArgs('b', "realmtech.items.stick"));
    }
}
