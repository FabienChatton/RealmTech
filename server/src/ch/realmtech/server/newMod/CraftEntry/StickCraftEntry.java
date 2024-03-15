package ch.realmtech.server.newMod.CraftEntry;

import ch.realmtech.server.newCraft.NewPatternArgs;
import ch.realmtech.server.newRegistry.NewCraftPatternShape;

public class StickCraftEntry extends NewCraftPatternShape {
    public StickCraftEntry() {
        super("StickCraft", "realmtech.items.Stick", 2, new char[][]{
                {'p'},
                {'p'}
        }, new NewPatternArgs('p', "realmtech.items.Plank"));
    }
}
