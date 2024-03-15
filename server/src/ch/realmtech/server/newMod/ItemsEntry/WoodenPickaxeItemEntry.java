package ch.realmtech.server.newMod.ItemsEntry;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.item.ItemType;
import ch.realmtech.server.newRegistry.NewItemEntry;

public class WoodenPickaxeItemEntry extends NewItemEntry {
    public WoodenPickaxeItemEntry() {
        super("WoodenPickaxe", "pioche-bois-01", ItemBehavior.builder()
                .setItemType(ItemType.PICKAXE)
                .build());
    }

    @Override
    public int getId() {
        return -1321815926;
    }
}
