package ch.realmtech.server.mod.items;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.registry.ItemEntry;

public class EnergyBatteryItemEntry extends ItemEntry {
    public EnergyBatteryItemEntry() {
        super("EnergyBattery", "energy-battery-01-0100", ItemBehavior.builder()
                .placeCell("realmtech.cells.EnergyBattery")
                .build());
    }
}
