package ch.realmtech.server.newMod.ItemsEntry;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.item.ItemType;
import ch.realmtech.server.newRegistry.NewItemEntry;

public class WoodenShovelItemEntry extends NewItemEntry {
    public WoodenShovelItemEntry() {
        super("woodenShovel", "pelle-bois-01", ItemBehavior.builder()
                .setItemType(ItemType.SHOVEL)
                .build());
    }

    @Override
    public int getId() {
        return -694017506;
    }
}
