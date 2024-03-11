package ch.realmtech.server.newMod.ItemsEntry;

import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.newRegistry.NewItemEntry;

public class EnergyBatteryItemEntry extends NewItemEntry {
    public EnergyBatteryItemEntry() {
        super("energyBattery", "energy-battery-01-0100", ItemBehavior.builder()
                .placeCell("realmtech.cells.energyBattery")
                .build());
    }
}
