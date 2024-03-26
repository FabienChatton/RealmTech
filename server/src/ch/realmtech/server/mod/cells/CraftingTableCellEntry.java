package ch.realmtech.server.mod.cells;

import ch.realmtech.server.ecs.component.CraftingTableComponent;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import ch.realmtech.server.inventory.AddAndDisplayInventoryArgs;
import ch.realmtech.server.inventory.DisplayInventoryArgs;
import ch.realmtech.server.item.ItemType;
import ch.realmtech.server.level.cell.CellBehavior;
import ch.realmtech.server.level.cell.Cells;
import ch.realmtech.server.level.cell.CreatePhysiqueBody;
import ch.realmtech.server.level.cell.EditEntity;
import ch.realmtech.server.mod.ClientContext;
import ch.realmtech.server.mod.factory.EditEntityFactory;
import ch.realmtech.server.packet.serverPacket.InventoryGetPacket;
import ch.realmtech.server.packet.serverPacket.SubscribeToEntityPacket;
import ch.realmtech.server.packet.serverPacket.UnSubscribeToEntityPacket;
import ch.realmtech.server.registry.CellEntry;
import ch.realmtech.server.registry.InvalideEvaluate;
import ch.realmtech.server.registry.Registry;
import ch.realmtech.server.registry.RegistryUtils;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class CraftingTableCellEntry extends CellEntry {
    private EditEntityFactory editEntityCreateCraftingTable;
    public CraftingTableCellEntry() {
        super("CraftingTable", "table-craft-01", CellBehavior.builder(Cells.Layer.BUILD_DECO)
                .breakWith(ItemType.HAND, "realmtech.items.CraftingTable")
                .physiqueBody(CreatePhysiqueBody.defaultPhysiqueBody())
                .canPlaceCellOnTop(false)
                .interagieClickDroit(CraftingTableCellEntry::rightClickInteraction)
                .build());
    }

    @Override
    public void evaluate(Registry<?> rootRegistry) throws InvalideEvaluate {
        super.evaluate(rootRegistry);
        editEntityCreateCraftingTable = RegistryUtils.evaluateSafe(rootRegistry, "realmtech.editEntity.Factory", EditEntityFactory.class);
    }

    @Override
    public Optional<EditEntity> getEditEntity() {
        return Optional.of(editEntityCreateCraftingTable.createCraftingTable(3, 3));
    }

    private static void rightClickInteraction(ClientContext clientContext, int cellId) {
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
            SystemsAdminCommun systemsAdminCommun = clientContext.getWorld().getRegistered("systemsAdmin");
            int inventoryPlayerId = systemsAdminCommun.getInventoryManager().getChestInventoryId(clientContext.getPlayerId());
            int inventoryCraftId = craftingTableComponent.craftingInventory;
            int inventoryResultId = craftingTableComponent.craftingResultInventory;
            UUID inventoryPlayerUuid = systemsAdminCommun.getUuidEntityManager().getEntityUuid(inventoryPlayerId);
            UUID inventoryCraftUuid = systemsAdminCommun.getUuidEntityManager().getEntityUuid(inventoryCraftId);
            UUID inventoryResultUuid = systemsAdminCommun.getUuidEntityManager().getEntityUuid(inventoryResultId);

            clientContext.sendRequest(new InventoryGetPacket(inventoryPlayerUuid));
            clientContext.sendRequest(new InventoryGetPacket(inventoryCraftUuid));
            clientContext.sendRequest(new InventoryGetPacket(inventoryResultUuid));

            // subscribe
            clientContext.sendRequest(new SubscribeToEntityPacket(inventoryCraftUuid));


            return new AddAndDisplayInventoryArgs(addTable, new DisplayInventoryArgs[]{
                    DisplayInventoryArgs.builder(systemsAdminCommun.getUuidEntityManager().getEntityUuid(inventoryPlayerId), playerInventory).build(),
                    DisplayInventoryArgs.builder(systemsAdminCommun.getUuidEntityManager().getEntityUuid(inventoryCraftId), craftingInventory).build(),
                    DisplayInventoryArgs.builder(systemsAdminCommun.getUuidEntityManager().getEntityUuid(inventoryResultId), craftingResultInventory).notClickAndDropDst().build()
            }, new UUID[]{inventoryCraftUuid, inventoryResultUuid}, () -> {
                clientContext.sendRequest(new UnSubscribeToEntityPacket(inventoryCraftUuid));
            });
        });
    }

    @Override
    public int getId() {
        return 129156771;
    }
}
