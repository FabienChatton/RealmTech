package ch.realmtech.server.mod.items;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.item.ItemType;
import ch.realmtech.server.registry.ItemEntry;

public class StoneShovelItemEntry extends ItemEntry {
    public StoneShovelItemEntry() {
        super("StoneShovel", "pelle-stone-01", ItemBehavior.builder()
                .setItemType(ItemType.SHOVEL)
                .build());
    }

    @Override
    public int getId() {
        return -23851682;
    }
}
