package ch.realmtech.server.mod.items;

import ch.realmtech.server.mod.factory.ItemBehaviorCommun;
import ch.realmtech.server.registry.ItemEntry;

public class StoneSwordItemEntry extends ItemEntry {
    public StoneSwordItemEntry() {
        super("StoneSword", "stone-sword-01", ItemBehaviorCommun
                .weapon(3)
                .build());
    }
}
