package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.game.ecs.component.EnergyGeneratorExtraInfoComponent;
import ch.realmtech.core.game.ecs.plugin.SystemsAdminClient;
import ch.realmtech.server.ecs.component.EnergyBatteryComponent;
import ch.realmtech.server.ecs.component.EnergyGeneratorComponent;
import ch.realmtech.server.ecs.component.EnergyGeneratorIconComponent;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.plugin.forclient.EnergyIconSystemForClient;
import ch.realmtech.server.mod.RealmTechCoreMod;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;

import java.util.UUID;

@All(EnergyGeneratorComponent.class)
public class EnergyGeneratorIconSystem extends IteratingSystem implements EnergyIconSystemForClient {

    @Wire
    private SystemsAdminClient systemsAdminClient;
    private ComponentMapper<EnergyBatteryComponent> mEnergyBattery;
    private ComponentMapper<EnergyGeneratorIconComponent> mEnergyBatteryIcon;
    private ComponentMapper<InventoryComponent> mInventory;
    private ComponentMapper<EnergyGeneratorExtraInfoComponent> mEnergyGeneratorExtraInfo;
    private ComponentMapper<EnergyGeneratorComponent> mEnergyGenerator;
    private ComponentMapper<EnergyGeneratorIconComponent> mEnergyGeneratorIcon;

    @Override
    protected void process(int entityId) {
        EnergyGeneratorComponent energyGeneratorComponent = mEnergyGenerator.get(entityId);
        EnergyGeneratorExtraInfoComponent energyGeneratorExtraInfoComponent = mEnergyGeneratorExtraInfo.get(entityId);
        EnergyGeneratorIconComponent energyGeneratorIconComponent = mEnergyGeneratorIcon.get(entityId);

        if (energyGeneratorComponent.getRemainingTickToBurn() > 0) {
            systemsAdminClient.getFurnaceIconSystem().setIcon(energyGeneratorIconComponent.getIconFireId(), "furnace-time-to-burn", energyGeneratorComponent.getRemainingTickToBurn(), energyGeneratorExtraInfoComponent.remainingTickToBurn);
        }
    }

    public void createEnergyGeneratorIcon(int motherId) {
        int iconFireId = world.create();

        systemsAdminClient.inventoryManager.createInventoryUiIcon(iconFireId, UUID.randomUUID(), new int[1][1], 1, 1);

        int iconFireItemId = systemsAdminClient.getItemManagerClient().newItemInventory(RealmTechCoreMod.ICON_FURNACE_TIME_TO_BURN_01, UUID.randomUUID());

        systemsAdminClient.inventoryManager.addItemToInventory(iconFireId, iconFireItemId);
        mEnergyBatteryIcon.create(motherId).set(iconFireId);
        mEnergyGeneratorExtraInfo.create(motherId);
    }


    @Override
    public void deleteGeneratorBatteryIcons(int entityId) {
        EnergyGeneratorIconComponent energyGeneratorIconComponent = mEnergyBatteryIcon.get(entityId);
        InventoryComponent inventoryIconFireComponent = mInventory.get(energyGeneratorIconComponent.getIconFireId());
        systemsAdminClient.inventoryManager.removeInventory(inventoryIconFireComponent.inventory);
        world.delete(energyGeneratorIconComponent.getIconFireId());

        systemsAdminClient.inventoryManager.removeInventory(systemsAdminClient.inventoryManager.getChestInventory(entityId).inventory);
    }

    public void setEnergyGeneratorExtraInfo(UUID energyGeneratorUuid, int remainingTickToBurn) {
        int energyGenerator = systemsAdminClient.uuidComponentManager.getRegisteredComponent(energyGeneratorUuid, EnergyGeneratorComponent.class);
        if (energyGenerator != -1) {
            mEnergyGeneratorExtraInfo.get(energyGenerator).remainingTickToBurn = remainingTickToBurn;
            mEnergyGenerator.get(energyGenerator).setRemainingTickToBurn(remainingTickToBurn);
        }
    }
}
