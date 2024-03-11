package ch.realmtech.server.newMod.ItemsEntry;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.item.ItemType;
import ch.realmtech.server.newRegistry.NewItemEntry;

public class StonePickaxeItemEntry extends NewItemEntry {
    public StonePickaxeItemEntry() {
        super("stonePickaxe", "pioche-stone-01", ItemBehavior.builder()
                .setItemType(ItemType.PICKAXE)
                .build());
    }
}
