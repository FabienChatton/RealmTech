package ch.realmtech.server.mod.items;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.item.ItemType;
import ch.realmtech.server.registry.ItemEntry;

public class WoodenShovelItemEntry extends ItemEntry {
    public WoodenShovelItemEntry() {
        super("WoodenShovel", "pelle-bois-01", ItemBehavior.builder()
                .setItemType(ItemType.SHOVEL)
                .build());
    }

    @Override
    public int getId() {
        return -694017506;
    }
}
