package ch.realmtech.server.newMod.ItemsEntry;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.item.ItemType;
import ch.realmtech.server.newRegistry.NewItemEntry;

public class WoodenPickaxeItemEntry extends NewItemEntry {
    public WoodenPickaxeItemEntry() {
        super("woodenPickaxe", "pioche-bois-01", ItemBehavior.builder()
                .setItemType(ItemType.PICKAXE)
                .build());
    }
}
