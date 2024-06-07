package ch.realmtech.server.mod.items;

import ch.realmtech.server.mod.factory.ItemBehaviorCommun;
import ch.realmtech.server.registry.ItemEntry;

public class WoodenSwordItemEntry extends ItemEntry {
    public WoodenSwordItemEntry() {
        super("WoodenSword", "wooden-sword-01", ItemBehaviorCommun
                .weapon(2)
                .build());
    }
}
