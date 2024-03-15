package ch.realmtech.server.newMod.CraftEntry;

import ch.realmtech.server.newCraft.NewPatternArgs;
import ch.realmtech.server.newRegistry.NewCraftPatternShape;

public class WoodenPickaxeCraftEntry extends NewCraftPatternShape {
    public WoodenPickaxeCraftEntry() {
        super("WoodenPickaxeCraft", "realmtech.items.WoodenPickaxe", 1, new char[][]{
                {'p', 'p', 'p'},
                {' ', 's', ' '},
                {' ', 's', ' '}
        }, new NewPatternArgs('p', "realmtech.items.Plank"), new NewPatternArgs('s', "realmtech.items.Stick"));
    }
}
