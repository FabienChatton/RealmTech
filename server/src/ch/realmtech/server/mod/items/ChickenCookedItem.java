package ch.realmtech.server.mod.items;

import ch.realmtech.server.mod.factory.ItemBehaviorCommun;
import ch.realmtech.server.registry.ItemEntry;

public class ChickenCookedItem extends ItemEntry {
    public ChickenCookedItem() {
        super("ChickenNuggets", "chicken-cooked", ItemBehaviorCommun
                .eat(4)
                .build());
    }

    @Override
    public int getId() {
        return 921868447;
    }
}
