package ch.realmtech.server.mod.cells;

import ch.realmtech.server.ecs.component.EnergyBatteryComponent;
import ch.realmtech.server.energy.EnergyBatteryEditEntity;
import ch.realmtech.server.item.ItemType;
import ch.realmtech.server.level.cell.CellBehavior;
import ch.realmtech.server.level.cell.Cells;
import ch.realmtech.server.level.cell.CreatePhysiqueBody;
import ch.realmtech.server.registry.CellEntry;

public class EnergyBatteryCellEntry extends CellEntry {
    public EnergyBatteryCellEntry() {
        super("EnergyBattery", "energy-battery-01-0100", CellBehavior.builder(Cells.Layer.BUILD_DECO)
                .breakWith(ItemType.HAND, "realmtech.items.EnergyBattery")
                .physiqueBody(CreatePhysiqueBody.defaultPhysiqueBody())
                .editEntity(EnergyBatteryEditEntity.createDefault())
                .canPlaceCellOnTop(false)
                .interagieClickDroit((clientContext, cellId, itemId) -> {
                    EnergyBatteryComponent energyBatteryComponent = clientContext.getWorld().getMapper(EnergyBatteryComponent.class).get(cellId);
                    clientContext.writeToConsole(Long.toString(energyBatteryComponent.getStored()));
                })
                .build());
    }

    @Override
    public int getId() {
        return 1670110684;
    }
}
