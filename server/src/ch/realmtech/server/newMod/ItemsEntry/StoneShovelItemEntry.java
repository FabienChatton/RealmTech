package ch.realmtech.server.newMod.ItemsEntry;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.item.ItemType;
import ch.realmtech.server.newRegistry.NewItemEntry;

public class StoneShovelItemEntry extends NewItemEntry {
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
