package ch.realmtech.server.newMod.CraftEntry;

import ch.realmtech.server.newCraft.NewPatternArgs;
import ch.realmtech.server.newRegistry.NewCraftPatternShape;

public class TorchCraftEntry extends NewCraftPatternShape {
    public TorchCraftEntry() {
        super("TorchCraft", "realmtech.items.Torch", 1, new char[][]{
                {'c'},
                {'s'}
        }, new NewPatternArgs('c', "realmtech.items.Coal"), new NewPatternArgs('s', "realmtech.items.Stick"));
    }
}
