package ch.realmtech.server.craft;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.ecs.component.*;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.ecs.system.InventoryManager;
import ch.realmtech.server.ecs.system.ItemManagerServer;
import ch.realmtech.server.item.ItemResultCraftPickEvent;
import ch.realmtech.server.packet.clientPacket.InventorySetPacket;
import com.artemis.ComponentMapper;
import com.artemis.World;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public final class OnNewCraftAvailable {
    public static Function<World, Function<CraftingTableComponent, Consumer<Optional<CraftResult>>>> onNewCraftAvailableCraftingTable() {
        return (world) -> {
            ComponentMapper<InventoryComponent> mInventory = world.getMapper(InventoryComponent.class);
            SystemsAdminServer systemsAdminServer = world.getRegistered(SystemsAdminServer.class);
            return (craftingTableComponent) -> {
                return (craftResultOpt) -> {
                    InventoryComponent resultInventoryComponent = mInventory.get(craftingTableComponent.craftingResultInventory);
                    if (craftResultOpt.isEmpty()) {
                        // remove result inventory because no craft is available
                        systemsAdminServer.inventoryManager.removeInventory(resultInventoryComponent.inventory);
                    } else {
                        CraftResult craftResult = craftResultOpt.get();
                        // add result item to result inventory
                        for (int i = 0; i < craftResult.getNombreResult(); i++) {
                            int nouvelItemResult = world.getSystem(ItemManagerServer.class).newItemInventory(craftResult.getItemRegisterEntry(), UUID.randomUUID());
                            world.edit(nouvelItemResult).create(ItemResultCraftComponent.class).set(ItemResultCraftPickEvent.removeAllOneItem(craftingTableComponent.craftingInventory));
                            world.getSystem(InventoryManager.class).addItemToStack(resultInventoryComponent.inventory[0], nouvelItemResult);
                        }
                    }
                    ServerContext serverContext = world.getRegistered("serverContext");
                    UUID craftingResultUuid = systemsAdminServer.uuidComponentManager.getRegisteredComponent(craftingTableComponent.craftingResultInventory).getUuid();

                    serverContext.getServerHandler().broadCastPacket(new InventorySetPacket(craftingResultUuid, serverContext.getSerializerController().getInventorySerializerManager().encode(mInventory.get(craftingTableComponent.craftingResultInventory))));
                };
            };
        };
    }

    public static Function<World, Function<CraftingTableComponent, Consumer<Optional<CraftResult>>>> onNewCraftAvailableFurnace() {
        return (world) -> (craftingTableComponent) -> (craftResultOpt) -> craftResultOpt.ifPresent((craftResult) -> {
            SystemsAdminServer systemsAdminServer = world.getRegistered(SystemsAdminServer.class);
            ComponentMapper<InventoryComponent> mInventory = world.getMapper(InventoryComponent.class);

            systemsAdminServer.inventoryManager.deleteOneItem(systemsAdminServer.inventoryManager.mInventory.get(craftingTableComponent.craftingInventory).inventory[0]);
            for (int i = 0; i < craftResult.getNombreResult(); i++) {
                int craftResultItemId = systemsAdminServer.itemManagerServer.newItemInventory(craftResult.getItemRegisterEntry(), UUID.randomUUID());
                systemsAdminServer.inventoryManager.addItemToInventory(craftingTableComponent.craftingResultInventory, craftResultItemId);
            }
            ServerContext serverContext = world.getRegistered("serverContext");
            UUID craftingInventoryUuid = systemsAdminServer.uuidComponentManager.getRegisteredComponent(craftingTableComponent.craftingInventory).getUuid();
            UUID craftingResultUuid = systemsAdminServer.uuidComponentManager.getRegisteredComponent(craftingTableComponent.craftingResultInventory).getUuid();

            serverContext.getServerHandler().broadCastPacket(new InventorySetPacket(craftingInventoryUuid, serverContext.getSerializerController().getInventorySerializerManager().encode(mInventory.get(craftingTableComponent.craftingInventory))));
            serverContext.getServerHandler().broadCastPacket(new InventorySetPacket(craftingResultUuid, serverContext.getSerializerController().getInventorySerializerManager().encode(mInventory.get(craftingTableComponent.craftingResultInventory))));
        });
    }
}
