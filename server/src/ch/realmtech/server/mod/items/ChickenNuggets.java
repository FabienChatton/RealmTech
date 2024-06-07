package ch.realmtech.server.mod.items;

import ch.realmtech.server.mod.factory.ItemBehaviorCommun;
import ch.realmtech.server.registry.ItemEntry;

public class ChickenNuggets extends ItemEntry {
    public ChickenNuggets() {
        super("ChickenNuggets", "copper-ingot-01", ItemBehaviorCommun
                .eat(4)
                .build());
    }
}
