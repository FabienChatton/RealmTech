package ch.realmtech.server.newMod.CellsEntry;

import ch.realmtech.server.ecs.component.CraftingTableComponent;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import ch.realmtech.server.ecs.system.InventoryManager;
import ch.realmtech.server.ecs.system.UuidEntityManager;
import ch.realmtech.server.inventory.AddAndDisplayInventoryArgs;
import ch.realmtech.server.inventory.DisplayInventoryArgs;
import ch.realmtech.server.item.ItemType;
import ch.realmtech.server.level.cell.CellBehavior;
import ch.realmtech.server.level.cell.Cells;
import ch.realmtech.server.level.cell.CraftingTableEditEntity;
import ch.realmtech.server.level.cell.CreatePhysiqueBody;
import ch.realmtech.server.newRegistry.NewCellEntry;
import ch.realmtech.server.packet.serverPacket.InventoryGetPacket;
import ch.realmtech.server.packet.serverPacket.SubscribeToEntityPacket;
import ch.realmtech.server.packet.serverPacket.UnSubscribeToEntityPacket;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

import java.util.UUID;
import java.util.function.Consumer;

public class CraftingTableCellEntry extends NewCellEntry {
    public CraftingTableCellEntry() {
        super("craftingTable", "table-craft-01", CellBehavior.builder(Cells.Layer.BUILD_DECO)
                .breakWith(ItemType.HAND, "realmtech.items.craftingTable")
                .physiqueBody(CreatePhysiqueBody.defaultPhysiqueBody())
                .editEntity(CraftingTableEditEntity.createCraftingTable(3, 3))
                .canPlaceCellOnTop(false)
                .interagieClickDroit((clientContext, cellId) -> {
                    ComponentMapper<CraftingTableComponent> mCrafting = clientContext.getWorld().getMapper(CraftingTableComponent.class);
                    ComponentMapper<InventoryComponent> mInventory = clientContext.getWorld().getMapper(InventoryComponent.class);
                    CraftingTableComponent craftingTableComponent = mCrafting.get(cellId);
                    clientContext.openPlayerInventory(() -> {
                        final Table playerInventory = new Table(clientContext.getSkin());
                        final Table craftingInventory = new Table(clientContext.getSkin());
                        final Table craftingResultInventory = new Table(clientContext.getSkin());
                        Consumer<Window> addTable = (window) -> {
                            Table craftingTable = new Table(craftingInventory.getSkin());
                            craftingTable.add(craftingInventory).padRight(32f);
                            craftingTable.add(craftingResultInventory);
                            craftingTable.padBottom(10f);
                            window.add(craftingTable).row();
                            window.add(playerInventory);
                        };
                        int inventoryPlayerId = clientContext.getWorld().getSystem(InventoryManager.class).getChestInventoryId(clientContext.getPlayerId());
                        int inventoryCraftId = craftingTableComponent.craftingInventory;
                        int inventoryResultId = craftingTableComponent.craftingResultInventory;
                        UUID inventoryPlayerUuid = clientContext.getWorld().getSystem(UuidEntityManager.class).getEntityUuid(inventoryPlayerId);
                        UUID inventoryCraftUuid = clientContext.getWorld().getSystem(UuidEntityManager.class).getEntityUuid(inventoryCraftId);
                        UUID inventoryResultUuid = clientContext.getWorld().getSystem(UuidEntityManager.class).getEntityUuid(inventoryResultId);

                        clientContext.sendRequest(new InventoryGetPacket(inventoryPlayerUuid));
                        clientContext.sendRequest(new InventoryGetPacket(inventoryCraftUuid));
                        clientContext.sendRequest(new InventoryGetPacket(inventoryResultUuid));

                        // subscribe
                        clientContext.sendRequest(new SubscribeToEntityPacket(inventoryCraftUuid));
                        SystemsAdminCommun systemsAdminCommun = clientContext.getWorld().getRegistered("systemsAdmin");

                        return new AddAndDisplayInventoryArgs(addTable, new DisplayInventoryArgs[]{
                                DisplayInventoryArgs.builder(systemsAdminCommun.uuidEntityManager.getEntityUuid(inventoryPlayerId), playerInventory).build(),
                                DisplayInventoryArgs.builder(systemsAdminCommun.uuidEntityManager.getEntityUuid(inventoryCraftId), craftingInventory).build(),
                                DisplayInventoryArgs.builder(systemsAdminCommun.uuidEntityManager.getEntityUuid(inventoryResultId), craftingResultInventory).notClickAndDropDst().build()
                        }, new UUID[]{inventoryCraftUuid, inventoryResultUuid}, () -> {
                            clientContext.sendRequest(new UnSubscribeToEntityPacket(inventoryCraftUuid));
                        });
                    });
                })
                .build());
    }
}
