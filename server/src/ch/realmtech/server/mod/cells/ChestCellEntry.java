package ch.realmtech.server.mod.cells;

import ch.realmtech.server.ecs.component.ChestComponent;
import ch.realmtech.server.inventory.AddAndDisplayInventoryArgs;
import ch.realmtech.server.inventory.DisplayInventoryArgs;
import ch.realmtech.server.item.ItemType;
import ch.realmtech.server.level.cell.CellBehavior;
import ch.realmtech.server.level.cell.Cells;
import ch.realmtech.server.level.cell.ChestEditEntity;
import ch.realmtech.server.level.cell.CreatePhysiqueBody;
import ch.realmtech.server.mod.ClientContext;
import ch.realmtech.server.packet.serverPacket.InventoryGetPacket;
import ch.realmtech.server.registry.CellEntry;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

import java.util.UUID;
import java.util.function.Consumer;

public class ChestCellEntry extends CellEntry {
    public ChestCellEntry() {
        super("Chest", "chest-01", CellBehavior.builder(Cells.Layer.BUILD_DECO)
                .breakWith(ItemType.HAND, "realmtech.items.Chest")
                .editEntity(ChestEditEntity.createNewInventory(9, 3))
                .physiqueBody(CreatePhysiqueBody.defaultPhysiqueBody())
                .canPlaceCellOnTop(false)
                .interagieClickDroit((clientContext, cellId, itemId) -> rightClickInteraction(clientContext, cellId))
                .build());
    }

    public static void rightClickInteraction(ClientContext clientContext, int cellId) {
        ComponentMapper<ChestComponent> mChest = clientContext.getWorld().getMapper(ChestComponent.class);
        ChestComponent chestComponent = mChest.get(cellId);
        clientContext.openPlayerInventory(() -> {
            final Table playerInventory = new Table(clientContext.getSkin());
            final Table inventory = new Table(clientContext.getSkin());

            Consumer<Window> addTable = window -> {
                window.add(inventory).padBottom(10f).row();
                window.add(playerInventory);
            };

            int inventoryPlayerId = clientContext.getSystemsAdminClient().getInventoryManager().getChestInventoryId(clientContext.getPlayerId());
            int inventoryChestId = chestComponent.getInventoryId();

            UUID inventoryPlayerUuid = clientContext.getSystemsAdminClient().getUuidEntityManager().getEntityUuid(inventoryPlayerId);
            UUID inventoryChestUuid = clientContext.getSystemsAdminClient().getUuidEntityManager().getEntityUuid(inventoryChestId);

            clientContext.sendRequest(new InventoryGetPacket(inventoryPlayerUuid));
            clientContext.sendRequest(new InventoryGetPacket(inventoryChestUuid));

            return new AddAndDisplayInventoryArgs(addTable, new DisplayInventoryArgs[]{
                    DisplayInventoryArgs.builder(clientContext.getSystemsAdminClient().getUuidEntityManager().getEntityUuid(inventoryPlayerId), playerInventory).build(),
                    DisplayInventoryArgs.builder(clientContext.getSystemsAdminClient().getUuidEntityManager().getEntityUuid(inventoryChestId), inventory).build()
            }, new UUID[]{inventoryChestUuid}, null);
        });
    }

    @Override
    public int getId() {
        return 1379107192;
    }
}
