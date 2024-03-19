package ch.realmtech.server.mod.items;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.level.WrenchRightClick;
import ch.realmtech.server.registry.ItemEntry;

public class WrenchItemEntry extends ItemEntry {
    public WrenchItemEntry() {
        super("Wrench", "wrench-01", ItemBehavior.builder()
                .interagieClickDroit(WrenchRightClick.wrenchRightClick())
                .build());
    }

    @Override
    public int getId() {
        return 384462994;
    }
}
