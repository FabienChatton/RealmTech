package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.ecs.component.*;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.packet.clientPacket.EnergyBatterySetEnergyPacket;
import ch.realmtech.server.packet.clientPacket.EnergyGeneratorInfoPacket;
import ch.realmtech.server.packet.clientPacket.InventorySetPacket;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;

import java.util.UUID;

@All({EnergyGeneratorComponent.class, EnergyBatteryComponent.class})
public class EnergyGeneratorSystem extends IteratingSystem {
    @Wire
    private SystemsAdminServer systemsAdminServer;
    @Wire(name = "serverContext")
    private ServerContext serverContext;
    private ComponentMapper<EnergyGeneratorComponent> mEnergyGenerator;
    private ComponentMapper<EnergyBatteryComponent> mEnergyBattery;
    private ComponentMapper<ItemComponent> mItem;
    private ComponentMapper<ChestComponent> mChest;
    private ComponentMapper<CellComponent> mCell;
    private ComponentMapper<InfChunkComponent> mChunk;
    private ComponentMapper<InventoryComponent> mInventory;

    @Override
    protected void process(int entityId) {
        EnergyGeneratorComponent energyGeneratorComponent = mEnergyGenerator.get(entityId);
        boolean newFuel = false;
        if (energyGeneratorComponent.getRemainingTickToBurn() <= 0) {
            int chestInventoryId = systemsAdminServer.getInventoryManager().getChestInventoryId(entityId);
            InventoryComponent carburantInventory = mInventory.get(chestInventoryId);

            UUID inventoryUuid = systemsAdminServer.getUuidEntityManager().getEntityUuid(chestInventoryId);
            int[] carburantStack = carburantInventory.inventory[0];
            int itemTemoinId = systemsAdminServer.getInventoryManager().getTopItem(carburantStack);
            if (mItem.has(itemTemoinId)) {
                ItemComponent itemComponent = mItem.get(itemTemoinId);
                int timeToBurn = itemComponent.itemRegisterEntry.getItemBehavior().getTimeToBurn();
                if (timeToBurn > 0) {
                    // burn item to fill this time to burn
                    energyGeneratorComponent.set(timeToBurn);
                    systemsAdminServer.getInventoryManager().deleteOneItem(carburantStack);
                    newFuel = true;


                    serverContext.getServerConnexion().sendPacketToSubscriberForEntity(new InventorySetPacket(inventoryUuid, serverContext.getSerializerController().getInventorySerializerManager().encode(carburantInventory)),
                            systemsAdminServer.getUuidEntityManager().getEntityUuid(entityId));
                }

            }
        }
        if (energyGeneratorComponent.getRemainingTickToBurn() > 0) {
            EnergyBatteryComponent energyBatteryComponent = mEnergyBattery.get(entityId);
            if (!energyBatteryComponent.isFull()) {
                energyGeneratorComponent.setRemainingTickToBurn(energyGeneratorComponent.getRemainingTickToBurn() - 1);
                energyBatteryComponent.addStored(1);

                UUID energyGeneratorUuid = systemsAdminServer.getUuidEntityManager().getEntityUuid(entityId);

                serverContext.getServerConnexion().sendPacketToSubscriberForEntity(new EnergyBatterySetEnergyPacket(energyGeneratorUuid, energyBatteryComponent.getStored()), systemsAdminServer.getUuidEntityManager().getEntityUuid(entityId));
                serverContext.getServerConnexion().sendPacketToSubscriberForEntity(new EnergyGeneratorInfoPacket(energyGeneratorUuid, energyGeneratorComponent.getRemainingTickToBurn(), newFuel ? energyGeneratorComponent.getRemainingTickToBurn() : -1), systemsAdminServer.getUuidEntityManager().getEntityUuid(entityId));
            }
        }
    }
}
