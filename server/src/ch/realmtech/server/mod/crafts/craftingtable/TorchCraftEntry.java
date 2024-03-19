package ch.realmtech.server.mod.crafts.craftingtable;

import ch.realmtech.server.craft.PatternArgs;
import ch.realmtech.server.registry.CraftPatternShape;

public class TorchCraftEntry extends CraftPatternShape {
    public TorchCraftEntry() {
        super("TorchCraft", "realmtech.items.Torch", 1, new char[][]{
                {'c'},
                {'s'}
        }, new PatternArgs('c', "realmtech.items.Coal"), new PatternArgs('s', "realmtech.items.Stick"));
    }
}
