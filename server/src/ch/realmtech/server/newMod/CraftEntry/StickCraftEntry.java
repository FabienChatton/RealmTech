package ch.realmtech.server.newMod.CraftEntry;

import ch.realmtech.server.newCraft.NewPatternArgs;
import ch.realmtech.server.newRegistry.NewCraftPatternShape;

public class StickCraftEntry extends NewCraftPatternShape {
    public StickCraftEntry() {
        super("stickCraft", "realmtech.items.stick", 2, new char[][]{
                {'p'},
                {'p'}
        }, new NewPatternArgs('p', "realmtech.items.plank"));
    }
}
