package ch.realmtech.server.newMod.ItemsEntry;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.item.ItemType;
import ch.realmtech.server.newRegistry.NewItemEntry;

public class StonePickaxeItemEntry extends NewItemEntry {
    public StonePickaxeItemEntry() {
        super("StonePickaxe", "pioche-stone-01", ItemBehavior.builder()
                .setItemType(ItemType.PICKAXE)
                .build());
    }

    @Override
    public int getId() {
        return 1989233778;
    }
}
