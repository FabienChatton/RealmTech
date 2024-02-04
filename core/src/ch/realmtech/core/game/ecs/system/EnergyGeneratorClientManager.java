package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.game.ecs.plugin.SystemsAdminClient;
import ch.realmtech.server.ecs.component.EnergyBatteryComponent;
import ch.realmtech.server.ecs.component.EnergyGeneratorComponent;
import ch.realmtech.server.ecs.component.EnergyGeneratorIconComponent;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.plugin.forclient.EnergyIconSystemForClient;
import ch.realmtech.server.mod.RealmTechCoreMod;
import com.artemis.ComponentMapper;
import com.artemis.Manager;
import com.artemis.annotations.Wire;

import java.util.UUID;

public class EnergyGeneratorClientManager extends Manager implements EnergyIconSystemForClient {

    @Wire
    private SystemsAdminClient systemsAdminClient;
    private ComponentMapper<EnergyBatteryComponent> mEnergyBattery;
    private ComponentMapper<EnergyGeneratorIconComponent> mEnergyBatteryIcon;
    private ComponentMapper<InventoryComponent> mInventory;
    private ComponentMapper<EnergyGeneratorComponent> mEnergyGenerator;
    private ComponentMapper<EnergyGeneratorIconComponent> mEnergyGeneratorIcon;

    public void createEnergyGeneratorIcon(int motherId, UUID iconInventoryUuid) {
        int iconFireId = world.create();
        EnergyGeneratorComponent energyGeneratorComponent = mEnergyGenerator.get(motherId);

        systemsAdminClient.inventoryManager.createInventoryUiIcon(iconFireId, iconInventoryUuid, new int[1][1], 1, 1);

        int iconFireItemId = systemsAdminClient.getItemManagerClient().newItemInventory(RealmTechCoreMod.ICON_FURNACE_TIME_TO_BURN_01, UUID.randomUUID());
        systemsAdminClient.inventoryManager.addItemToInventory(iconFireId, iconFireItemId);

        mEnergyBatteryIcon.create(motherId).set(iconFireId);
        systemsAdminClient.getFurnaceIconSystem().setIcon(iconFireId, "furnace-time-to-burn", energyGeneratorComponent.getRemainingTickToBurn(), energyGeneratorComponent.getLastRemainingTickToBurn());
    }


    @Override
    public void deleteGeneratorBatteryIcons(int entityId) {
        EnergyGeneratorIconComponent energyGeneratorIconComponent = mEnergyBatteryIcon.get(entityId);
        InventoryComponent inventoryIconFireComponent = mInventory.get(energyGeneratorIconComponent.getIconFireId());

        systemsAdminClient.inventoryManager.removeInventoryUi(energyGeneratorIconComponent.getIconFireId());
        world.delete(energyGeneratorIconComponent.getIconFireId());
    }

    public void setEnergy(int energyBatteryId, long stored) {
        mEnergyBattery.get(energyBatteryId).setStored(stored);
    }
}
