package ch.realmtech.server.newMod.CraftEntry;

import ch.realmtech.server.newCraft.NewPatternArgs;
import ch.realmtech.server.newRegistry.NewCraftPatternShape;

public class FurnaceCraftEntry extends NewCraftPatternShape {
    public FurnaceCraftEntry() {
        super("FurnaceCraft", "realmtech.items.Furnace", 1, new char[][]{
                {'p', 'p', 'p'},
                {'p', ' ', 'p'},
                {'p', 'p', 'p'}
        }, new NewPatternArgs('p', "realmtech.items.StoneOre"));
    }
}
