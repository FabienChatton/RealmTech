package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.ecs.component.*;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.packet.clientPacket.CellSetPacket;
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
    private ComponentMapper<InventoryComponent> mInventory;
    private ComponentMapper<CellComponent> mCell;
    private ComponentMapper<InfChunkComponent> mChunk;

    @Override
    protected void process(int entityId) {
        EnergyGeneratorComponent energyGeneratorComponent = mEnergyGenerator.get(entityId);
        if (energyGeneratorComponent.getRemainingTickToBurn() <= 0) {
            InventoryComponent carburantInventory = mInventory.get(entityId);
            UUID inventoryUuid = systemsAdminServer.uuidComponentManager.getRegisteredComponent(entityId).getUuid();
            int[] stack = carburantInventory.inventory[0];
            int itemTemoinId = systemsAdminServer.inventoryManager.getTopItem(stack);
            if (mItem.has(itemTemoinId)) {
                ItemComponent itemComponent = mItem.get(itemTemoinId);
                int timeToBurn = itemComponent.itemRegisterEntry.getItemBehavior().getTimeToBurn();
                if (timeToBurn > 0) {
                    energyGeneratorComponent.setRemainingTickToBurn(timeToBurn);
                }
                systemsAdminServer.inventoryManager.deleteOneItem(stack);
                serverContext.getServerHandler().broadCastPacket(new InventorySetPacket(inventoryUuid, serverContext.getSerializerController().getInventorySerializerManager().encode(carburantInventory)));
            }
        } else {
            EnergyBatteryComponent energyBatteryComponent = mEnergyBattery.get(entityId);
            if (!energyBatteryComponent.isFull()) {
                energyGeneratorComponent.setRemainingTickToBurn(energyGeneratorComponent.getRemainingTickToBurn() - 1);
                energyBatteryComponent.addStored(1);
                CellComponent cellComponent = mCell.get(entityId);
                int worldPosX = systemsAdminServer.mapManager.getWorldPosX(cellComponent);
                int worldPosY = systemsAdminServer.mapManager.getWorldPosY(cellComponent);
                serverContext.getServerHandler().broadCastPacket(new CellSetPacket(worldPosX, worldPosY, cellComponent.cellRegisterEntry.getCellBehavior().getLayer(), serverContext.getSerializerController().getCellSerializerController().encode(entityId)));
            }
        }
    }
}
