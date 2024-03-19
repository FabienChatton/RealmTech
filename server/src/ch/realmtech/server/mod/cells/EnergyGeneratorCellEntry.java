package ch.realmtech.server.mod.cells;

import ch.realmtech.server.ecs.component.ChestComponent;
import ch.realmtech.server.ecs.component.EnergyGeneratorIconComponent;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import ch.realmtech.server.ecs.system.InventoryManager;
import ch.realmtech.server.ecs.system.UuidEntityManager;
import ch.realmtech.server.energy.EnergyGeneratorEditEntity;
import ch.realmtech.server.inventory.AddAndDisplayInventoryArgs;
import ch.realmtech.server.inventory.DisplayInventoryArgs;
import ch.realmtech.server.item.ItemType;
import ch.realmtech.server.level.cell.CellBehavior;
import ch.realmtech.server.level.cell.Cells;
import ch.realmtech.server.level.cell.ChestEditEntity;
import ch.realmtech.server.level.cell.CreatePhysiqueBody;
import ch.realmtech.server.packet.serverPacket.InventoryGetPacket;
import ch.realmtech.server.packet.serverPacket.SubscribeToEntityPacket;
import ch.realmtech.server.packet.serverPacket.UnSubscribeToEntityPacket;
import ch.realmtech.server.registry.CellEntry;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

import java.util.UUID;
import java.util.function.Consumer;

public class EnergyGeneratorCellEntry extends CellEntry {
    public EnergyGeneratorCellEntry() {
        super("EnergyGenerator", "furnace-01", CellBehavior.builder(Cells.Layer.BUILD_DECO)
                .breakWith(ItemType.HAND, "realmtech.items.EnergyGenerator")
                .editEntity(EnergyGeneratorEditEntity.createDefault(), ChestEditEntity.createNewInventory(1, 1))
                .physiqueBody(CreatePhysiqueBody.defaultPhysiqueBody())
                .canPlaceCellOnTop(false)
                .interagieClickDroit((clientContext, cellId) -> {
                    ComponentMapper<InventoryComponent> mInventory = clientContext.getWorld().getMapper(InventoryComponent.class);
                    ComponentMapper<ChestComponent> mChest = clientContext.getWorld().getMapper(ChestComponent.class);
                    ComponentMapper<EnergyGeneratorIconComponent> mEnergyBatteryIcon = clientContext.getWorld().getMapper(EnergyGeneratorIconComponent.class);

                    int carburantInventoryId = mChest.get(cellId).getInventoryId();
                    InventoryComponent carburantInventory = mInventory.get(carburantInventoryId);
                    EnergyGeneratorIconComponent energyGeneratorIconComponent = mEnergyBatteryIcon.get(cellId);
                    clientContext.openPlayerInventory(() -> {
                        Table playerInventory = new Table(clientContext.getSkin());
                        Table energyGeneratorInventory = new Table(clientContext.getSkin());
                        Table iconFire = new Table(clientContext.getSkin());

                        Consumer<Window> addTable = window -> {
                            window.add(iconFire).padBottom(2f).row();
                            window.add(energyGeneratorInventory).padBottom(10f).row();
                            window.add(playerInventory);
                        };

                        int inventoryPlayerId = clientContext.getWorld().getSystem(InventoryManager.class).getChestInventoryId(clientContext.getPlayerId());
                        int fireIconId = energyGeneratorIconComponent.getIconFireId();
                        SystemsAdminCommun systemsAdminCommun = clientContext.getWorld().getRegistered("systemsAdmin");
                        // subscription
                        UUID energyGeneratorUuid = systemsAdminCommun.uuidEntityManager.getEntityUuid(cellId);
                        clientContext.sendRequest(new SubscribeToEntityPacket(energyGeneratorUuid));

                        // inventory get
                        UUID inventoryPlayerUuid = clientContext.getWorld().getSystem(UuidEntityManager.class).getEntityUuid(inventoryPlayerId);
                        UUID inventoryChestUuid = clientContext.getWorld().getSystem(UuidEntityManager.class).getEntityUuid(carburantInventoryId);
                        clientContext.sendRequest(new InventoryGetPacket(inventoryPlayerUuid));
                        clientContext.sendRequest(new InventoryGetPacket(inventoryChestUuid));

                        return new AddAndDisplayInventoryArgs(addTable, new DisplayInventoryArgs[]{
                                DisplayInventoryArgs.builder(systemsAdminCommun.uuidEntityManager.getEntityUuid(inventoryPlayerId), playerInventory).build(),
                                DisplayInventoryArgs.builder(systemsAdminCommun.uuidEntityManager.getEntityUuid(carburantInventoryId), energyGeneratorInventory).build(),
                                DisplayInventoryArgs.builder(systemsAdminCommun.uuidEntityManager.getEntityUuid(fireIconId), iconFire).icon().build()
                        }, new UUID[]{inventoryChestUuid}, () -> {
                            clientContext.sendRequest(new UnSubscribeToEntityPacket(energyGeneratorUuid));
                        });
                    });
                })
                .build());
    }

    @Override
    public int getId() {
        return -560459038;
    }
}
