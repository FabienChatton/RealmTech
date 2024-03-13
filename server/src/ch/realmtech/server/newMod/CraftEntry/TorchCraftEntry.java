package ch.realmtech.server.newMod.CraftEntry;

import ch.realmtech.server.newCraft.NewPatternArgs;
import ch.realmtech.server.newRegistry.NewCraftPatternShape;

public class TorchCraftEntry extends NewCraftPatternShape {
    public TorchCraftEntry() {
        super("torchCraft", "realmtech.items.torch", 1, new char[][]{
                {'c'},
                {'s'}
        }, new NewPatternArgs('c', "realmtech.items.coal"), new NewPatternArgs('s', "realmtech.items.stick"));
    }
}
