package ch.realmtech.server.mod.crafts.craftingtable;

import ch.realmtech.server.craft.PatternArgs;
import ch.realmtech.server.registry.CraftPatternShape;

public class StonePickaxeStoneCraftEntry extends CraftPatternShape {
    public StonePickaxeStoneCraftEntry() {
        super("StonePickaxeCraft", "realmtech.items.StonePickaxe", 1, new char[][]{
                {'p', 'p', 'p'},
                {' ', 's', ' '},
                {' ', 's', ' '}
        }, new PatternArgs('p', "realmtech.items.StoneOre"), new PatternArgs('s', "realmtech.items.Stick"));
    }
}
