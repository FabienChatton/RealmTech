package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.craft.CraftResult;
import ch.realmtech.server.ctrl.ItemManager;
import ch.realmtech.server.ecs.component.CraftingTableComponent;
import ch.realmtech.server.ecs.component.FurnaceComponent;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.component.ItemComponent;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.packet.clientPacket.FurnaceExtraInfoPacket;
import ch.realmtech.server.packet.clientPacket.InventorySetPacket;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;

import java.util.Optional;
import java.util.UUID;

@All({FurnaceComponent.class, CraftingTableComponent.class})
public class FurnaceSystem extends IteratingSystem {
    @Wire
    private ItemManager itemManager;
    @Wire
    private SystemsAdminServer systemsAdminServer;
    @Wire(name = "serverContext")
    private ServerContext serverContext;
    private ComponentMapper<FurnaceComponent> mFurnace;
    private ComponentMapper<CraftingTableComponent> mCraftingTable;
    private ComponentMapper<ItemComponent> mItem;
    private ComponentMapper<InventoryComponent> mInventory;

    @Override
    protected void process(int entityId) {
        FurnaceComponent furnaceComponent = mFurnace.get(entityId);
        CraftingTableComponent craftingTableComponent = mCraftingTable.get(entityId);
        InventoryComponent carburantInventoryComponent = mInventory.get(furnaceComponent.inventoryCarburant);

        if (furnaceComponent.remainingTickToBurn > 0) {
            furnaceComponent.remainingTickToBurn--;
        }

        // met à jour le carburant
        if (furnaceComponent.remainingTickToBurn <= 0) {
            int[] carburantStack = carburantInventoryComponent.inventory[0];
            int carburantItemId = systemsAdminServer.inventoryManager.getTopItem(carburantStack);

            ItemComponent carburantItemComponent = mItem.get(carburantItemId);
            Optional<CraftResult> craftResult = systemsAdminServer.craftingManager.getCraftResult(craftingTableComponent);
            if (carburantItemComponent != null && craftResult.isPresent()) {
                if (carburantItemComponent.itemRegisterEntry.getItemBehavior().getTimeToBurn() > 0) {
                    furnaceComponent.remainingTickToBurn = carburantItemComponent.itemRegisterEntry.getItemBehavior().getTimeToBurn();
                    systemsAdminServer.inventoryManager.deleteItemInStack(carburantStack, carburantItemId);

                    UUID carburantInventoryUuid = systemsAdminServer.uuidEntityManager.getEntityUuid(furnaceComponent.inventoryCarburant);
                    serverContext.getServerConnexion().sendPacketToSubscriberForEntityId(
                            new InventorySetPacket(carburantInventoryUuid, serverContext.getSerializerController().getInventorySerializerManager().encode(carburantInventoryComponent)),
                            entityId
                    );


                    serverContext.getServerConnexion().sendPacketToSubscriberForEntityId(new FurnaceExtraInfoPacket(
                            systemsAdminServer.uuidEntityManager.getEntityUuid(entityId),
                            furnaceComponent.remainingTickToBurn,
                            -1
                    ), entityId);
                }
            }
        }
    }
}
