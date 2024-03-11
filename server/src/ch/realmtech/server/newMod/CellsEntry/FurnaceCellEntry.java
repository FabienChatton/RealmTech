package ch.realmtech.server.newMod.CellsEntry;

import ch.realmtech.server.ecs.component.CraftingTableComponent;
import ch.realmtech.server.ecs.component.FurnaceComponent;
import ch.realmtech.server.ecs.component.FurnaceIconsComponent;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import ch.realmtech.server.ecs.system.InventoryManager;
import ch.realmtech.server.inventory.AddAndDisplayInventoryArgs;
import ch.realmtech.server.inventory.DisplayInventoryArgs;
import ch.realmtech.server.item.ItemType;
import ch.realmtech.server.level.cell.CellBehavior;
import ch.realmtech.server.level.cell.Cells;
import ch.realmtech.server.level.cell.CreatePhysiqueBody;
import ch.realmtech.server.level.cell.FurnaceEditEntity;
import ch.realmtech.server.newRegistry.NewCellEntry;
import ch.realmtech.server.packet.serverPacket.InventoryGetPacket;
import ch.realmtech.server.packet.serverPacket.SubscribeToEntityPacket;
import ch.realmtech.server.packet.serverPacket.UnSubscribeToEntityPacket;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

import java.util.UUID;
import java.util.function.Consumer;

public class FurnaceCellEntry extends NewCellEntry {
    public FurnaceCellEntry() {
        super("furnace", "furnace-01", CellBehavior.builder(Cells.Layer.BUILD_DECO)
                .breakWith(ItemType.HAND, "realmtech.items.furnace")
                .physiqueBody(CreatePhysiqueBody.defaultPhysiqueBody())
                .editEntity(FurnaceEditEntity.createFurnace())
                .canPlaceCellOnTop(false)
                .interagieClickDroit((clientContext, cellId) -> {
                    ComponentMapper<CraftingTableComponent> mCrafting = clientContext.getWorld().getMapper(CraftingTableComponent.class);
                    ComponentMapper<InventoryComponent> mInventory = clientContext.getWorld().getMapper(InventoryComponent.class);
                    ComponentMapper<FurnaceComponent> mFurnace = clientContext.getWorld().getMapper(FurnaceComponent.class);
                    ComponentMapper<FurnaceIconsComponent> mFurnaceIcons = clientContext.getWorld().getMapper(FurnaceIconsComponent.class);

                    CraftingTableComponent craftingTableComponent = mCrafting.get(cellId);
                    FurnaceComponent furnaceComponent = mFurnace.get(cellId);
                    FurnaceIconsComponent furnaceIconsComponent = mFurnaceIcons.get(cellId);
                    clientContext.openPlayerInventory(() -> {
                        Table playerInventory = new Table(clientContext.getSkin());
                        Table craftingInventory = new Table(clientContext.getSkin());
                        Table craftingResultInventory = new Table(clientContext.getSkin());
                        Table carburantInventory = new Table(clientContext.getSkin());
                        Table iconFire = new Table(clientContext.getSkin());
                        Table iconProcess = new Table(clientContext.getSkin());

                        Consumer<Window> addTable = (window) -> {
                            Table craftingTable = new Table(craftingInventory.getSkin());
                            craftingTable.add(craftingInventory);
                            craftingTable.row();

                            craftingTable.add(iconFire);
                            craftingTable.add(iconProcess).padLeft(32f);
                            craftingTable.add(craftingResultInventory).padLeft(32f);
                            craftingTable.row();

                            craftingTable.add(carburantInventory);
                            craftingTable.row();

                            craftingTable.padBottom(10f);
                            window.add(craftingTable).row();
                            window.add(playerInventory);
                        };
                        int inventoryPlayerId = clientContext.getWorld().getSystem(InventoryManager.class).getChestInventoryId(clientContext.getPlayerId());
                        int inventoryCraftId = craftingTableComponent.craftingInventory;
                        int inventoryResultId = craftingTableComponent.craftingResultInventory;
                        int inventoryCarburantId = furnaceComponent.inventoryCarburant;
                        int iconFireId = furnaceIconsComponent.getIconFire();
                        int iconProcessId = furnaceIconsComponent.getIconProcess();
                        SystemsAdminCommun systemsAdminCommun = clientContext.getWorld().getRegistered("systemsAdmin");

                        // subscription
                        UUID furnaceUuid = systemsAdminCommun.uuidEntityManager.getEntityUuid(cellId);
                        clientContext.sendRequest(new SubscribeToEntityPacket(furnaceUuid));

                        // get inventory
                        UUID inventoryPlayerUuid = systemsAdminCommun.uuidEntityManager.getEntityUuid(inventoryPlayerId);
                        UUID inventoryCraftUuid = systemsAdminCommun.uuidEntityManager.getEntityUuid(inventoryCraftId);
                        UUID inventoryResultUuid = systemsAdminCommun.uuidEntityManager.getEntityUuid(inventoryResultId);
                        UUID inventoryCarburantUuid = systemsAdminCommun.uuidEntityManager.getEntityUuid(inventoryCarburantId);

                        UUID iconFireUuid = systemsAdminCommun.uuidEntityManager.getEntityUuid(iconFireId);
                        UUID iconProcessUuid = systemsAdminCommun.uuidEntityManager.getEntityUuid(iconProcessId);

                        clientContext.sendRequest(new InventoryGetPacket(inventoryPlayerUuid));
                        clientContext.sendRequest(new InventoryGetPacket(inventoryCraftUuid));
                        clientContext.sendRequest(new InventoryGetPacket(inventoryResultUuid));
                        clientContext.sendRequest(new InventoryGetPacket(inventoryCarburantUuid));

                        return new AddAndDisplayInventoryArgs(addTable, new DisplayInventoryArgs[]{
                                DisplayInventoryArgs.builder(inventoryPlayerUuid, playerInventory).build(),
                                DisplayInventoryArgs.builder(inventoryCraftUuid, craftingInventory).dstRequire(FurnaceEditEntity.testValideItemForCraft()).build(),
                                DisplayInventoryArgs.builder(inventoryResultUuid, craftingResultInventory).notClickAndDropDst().build(),
                                DisplayInventoryArgs.builder(inventoryCarburantUuid, carburantInventory).dstRequire(FurnaceEditEntity.testValideItemCarburant()).build(),
                                // icons
                                DisplayInventoryArgs.builder(iconFireUuid, iconFire).icon().build(),
                                DisplayInventoryArgs.builder(iconProcessUuid, iconProcess).icon().build()
                        }, new UUID[]{inventoryCraftUuid, inventoryResultUuid, inventoryCarburantUuid}, () -> {
                            clientContext.sendRequest(new UnSubscribeToEntityPacket(furnaceUuid));
                        });
                    });
                })
                .build());
    }
}
