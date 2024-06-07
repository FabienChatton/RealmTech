package ch.realmtech.server.mod.items;

import ch.realmtech.server.mod.factory.ItemBehaviorCommun;
import ch.realmtech.server.registry.ItemEntry;

public class WeaponItemEntry extends ItemEntry {
    public WeaponItemEntry() {
        super("Weapon", "p2022", ItemBehaviorCommun
                .weapon(5, 10, true)
                .build());
    }
}
