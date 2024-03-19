package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.game.ecs.component.EnergyGeneratorExtraInfoComponent;
import ch.realmtech.core.game.ecs.plugin.SystemsAdminClient;
import ch.realmtech.server.ecs.component.EnergyBatteryComponent;
import ch.realmtech.server.ecs.component.EnergyGeneratorComponent;
import ch.realmtech.server.ecs.component.EnergyGeneratorIconComponent;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.plugin.forclient.EnergyIconSystemForClient;
import ch.realmtech.server.mod.icons.FurnaceBurnIcon01Entry;
import ch.realmtech.server.registry.RegistryUtils;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;

import java.util.UUID;

@All({EnergyGeneratorComponent.class, EnergyGeneratorExtraInfoComponent.class})
public class EnergyGeneratorClientSystem extends IteratingSystem implements EnergyIconSystemForClient {

    @Wire
    private SystemsAdminClient systemsAdminClient;
    private ComponentMapper<EnergyBatteryComponent> mEnergyBattery;
    private ComponentMapper<EnergyGeneratorIconComponent> mEnergyGeneratorIcon;
    private ComponentMapper<InventoryComponent> mInventory;
    private ComponentMapper<EnergyGeneratorComponent> mEnergyGenerator;
    private ComponentMapper<EnergyGeneratorExtraInfoComponent> mEnergyGeneratorExtraInfo;

    @Override
    protected void process(int entityId) {
        EnergyGeneratorIconComponent energyGeneratorIconComponent = mEnergyGeneratorIcon.get(entityId);
        EnergyGeneratorComponent energyGeneratorComponent = mEnergyGenerator.get(entityId);
        EnergyGeneratorExtraInfoComponent energyGeneratorExtraInfoComponent = mEnergyGeneratorExtraInfo.get(entityId);
        systemsAdminClient.getFurnaceIconSystem().setIcon(energyGeneratorIconComponent.getIconFireId(), "furnace-time-to-burn", Math.max(1, energyGeneratorComponent.getRemainingTickToBurn()), energyGeneratorExtraInfoComponent.getLastRemainingTickToBurn());
    }

    public void createEnergyGeneratorIcon(int motherId, UUID iconInventoryUuid) {
        int iconFireId = world.create();
        EnergyGeneratorComponent energyGeneratorComponent = mEnergyGenerator.get(motherId);

        systemsAdminClient.inventoryManager.createInventoryUiIcon(iconFireId, iconInventoryUuid, new int[1][1], 1, 1);

        int iconFireItemId = systemsAdminClient.getItemManagerClient().newItemInventory(RegistryUtils.findEntryOrThrow(systemsAdminClient.rootRegistry, FurnaceBurnIcon01Entry.class), UUID.randomUUID());
        systemsAdminClient.inventoryManager.addItemToInventory(iconFireId, iconFireItemId);

        mEnergyGeneratorIcon.create(motherId).set(iconFireId);
        EnergyGeneratorExtraInfoComponent energyGeneratorExtraInfoComponent = mEnergyGeneratorExtraInfo.create(motherId).set(0);
        systemsAdminClient.getFurnaceIconSystem().setIcon(iconFireId, "furnace-time-to-burn", energyGeneratorComponent.getRemainingTickToBurn(), energyGeneratorExtraInfoComponent.getLastRemainingTickToBurn());
    }

    @Override
    public void deleteGeneratorBatteryIcons(int entityId) {
        EnergyGeneratorIconComponent energyGeneratorIconComponent = mEnergyGeneratorIcon.get(entityId);
        InventoryComponent inventoryIconFireComponent = mInventory.get(energyGeneratorIconComponent.getIconFireId());

        systemsAdminClient.inventoryManager.removeInventoryUi(energyGeneratorIconComponent.getIconFireId());
        world.delete(energyGeneratorIconComponent.getIconFireId());
    }

    public void setEnergy(int energyBatteryId, long stored) {
        if (energyBatteryId != -1) {
            mEnergyBattery.get(energyBatteryId).setStored(stored);
        }
    }

    public void setEnergyGeneratorInfo(int energyGeneratorId, int remainingTickToBurn, int lastRemainingTickToBurn) {
        mEnergyGenerator.get(energyGeneratorId).set(remainingTickToBurn);
        if (lastRemainingTickToBurn != -1) {
            mEnergyGeneratorExtraInfo.get(energyGeneratorId).set(lastRemainingTickToBurn);
        }
    }
}
