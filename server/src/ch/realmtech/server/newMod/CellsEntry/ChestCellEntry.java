package ch.realmtech.server.newMod.CellsEntry;

import ch.realmtech.server.ecs.component.ChestComponent;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import ch.realmtech.server.ecs.system.InventoryManager;
import ch.realmtech.server.ecs.system.UuidEntityManager;
import ch.realmtech.server.inventory.AddAndDisplayInventoryArgs;
import ch.realmtech.server.inventory.DisplayInventoryArgs;
import ch.realmtech.server.item.ItemType;
import ch.realmtech.server.level.cell.CellBehavior;
import ch.realmtech.server.level.cell.Cells;
import ch.realmtech.server.level.cell.ChestEditEntity;
import ch.realmtech.server.level.cell.CreatePhysiqueBody;
import ch.realmtech.server.newRegistry.NewCellEntry;
import ch.realmtech.server.packet.serverPacket.InventoryGetPacket;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

import java.util.UUID;
import java.util.function.Consumer;

public class ChestCellEntry extends NewCellEntry {
    public ChestCellEntry() {
        super("chest", "chest-01", CellBehavior.builder(Cells.Layer.BUILD_DECO)
                .breakWith(ItemType.HAND, "realmtech.items.chest")
                .editEntity(ChestEditEntity.createNewInventory(9, 3))
                .physiqueBody(CreatePhysiqueBody.defaultPhysiqueBody())
                .canPlaceCellOnTop(false)
                .interagieClickDroit((clientContext, cellId) -> {
                    ComponentMapper<ChestComponent> mChest = clientContext.getWorld().getMapper(ChestComponent.class);
                    ChestComponent chestComponent = mChest.get(cellId);
                    clientContext.openPlayerInventory(() -> {
                        final Table playerInventory = new Table(clientContext.getSkin());
                        final Table inventory = new Table(clientContext.getSkin());

                        Consumer<Window> addTable = window -> {
                            window.add(inventory).padBottom(10f).row();
                            window.add(playerInventory);
                        };

                        int inventoryPlayerId = clientContext.getWorld().getSystem(InventoryManager.class).getChestInventoryId(clientContext.getPlayerId());
                        int inventoryChestId = chestComponent.getInventoryId();

                        UUID inventoryPlayerUuid = clientContext.getWorld().getSystem(UuidEntityManager.class).getEntityUuid(inventoryPlayerId);
                        UUID inventoryChestUuid = clientContext.getWorld().getSystem(UuidEntityManager.class).getEntityUuid(inventoryChestId);

                        clientContext.sendRequest(new InventoryGetPacket(inventoryPlayerUuid));
                        clientContext.sendRequest(new InventoryGetPacket(inventoryChestUuid));

                        SystemsAdminCommun systemsAdminCommun = clientContext.getWorld().getRegistered("systemsAdmin");

                        return new AddAndDisplayInventoryArgs(addTable, new DisplayInventoryArgs[]{
                                DisplayInventoryArgs.builder(systemsAdminCommun.uuidEntityManager.getEntityUuid(inventoryPlayerId), playerInventory).build(),
                                DisplayInventoryArgs.builder(systemsAdminCommun.uuidEntityManager.getEntityUuid(inventoryChestId), inventory).build()
                        }, new UUID[]{inventoryChestUuid}, null);
                    });
                })
                .build());
    }
}
