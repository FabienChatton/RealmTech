package ch.realmtech.server.mod.items;

import ch.realmtech.server.mod.factory.ItemBehaviorCommun;
import ch.realmtech.server.registry.ItemEntry;

public class RawChickenItemEntry extends ItemEntry {
    public RawChickenItemEntry() {
        super("RawChicken", "raw-chicken", ItemBehaviorCommun
                .eat(2)
                .build());
    }
}
