package ch.realmtech.server.newMod.CraftEntry;

import ch.realmtech.server.newCraft.NewPatternArgs;
import ch.realmtech.server.newRegistry.NewCraftPatternShape;

public class StonePickaxeStoneCraftEntry extends NewCraftPatternShape {
    public StonePickaxeStoneCraftEntry() {
        super("stonePickaxeCraft", "realmtech.items.stonePickaxe", 1, new char[][]{
                {'p', 'p', 'p'},
                {' ', 's', ' '},
                {' ', 's', ' '}
        }, new NewPatternArgs('p', "realmtech.items.stoneOre"), new NewPatternArgs('s', "realmtech.items.stick"));
    }
}
