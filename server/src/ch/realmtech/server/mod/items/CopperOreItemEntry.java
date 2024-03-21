package ch.realmtech.server.mod.items;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.registry.ItemEntry;

public class CopperOreItemEntry extends ItemEntry {
    public CopperOreItemEntry() {
        super("CopperOre", "copper-ore-03", ItemBehavior.builder()
                .build());
    }
}
