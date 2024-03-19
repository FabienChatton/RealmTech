package ch.realmtech.server.mod.items;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.item.ItemType;
import ch.realmtech.server.registry.ItemEntry;

public class StonePickaxeItemEntry extends ItemEntry {
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
