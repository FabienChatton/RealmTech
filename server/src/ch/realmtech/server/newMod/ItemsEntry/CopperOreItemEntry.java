package ch.realmtech.server.newMod.ItemsEntry;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.newRegistry.NewItemEntry;

public class CopperOreItemEntry extends NewItemEntry {
    public CopperOreItemEntry() {
        super("copperOre", "copper-ore-03", ItemBehavior.builder()
                .build());
    }

    @Override
    public int getId() {
        return 947251712;
    }
}
