package ch.realmtech.server.newMod.CraftEntry;

import ch.realmtech.server.newCraft.NewPatternArgs;
import ch.realmtech.server.newRegistry.NewCraftPatternShape;

public class FurnaceCraftEntry extends NewCraftPatternShape {
    public FurnaceCraftEntry() {
        super("furnaceCraft", "realmtech.items.furnace", 1, new char[][]{
                {'p', 'p', 'p'},
                {'p', ' ', 'p'},
                {'p', 'p', 'p'}
        }, new NewPatternArgs('p', "realmtech.items.stoneOre"));
    }
}
