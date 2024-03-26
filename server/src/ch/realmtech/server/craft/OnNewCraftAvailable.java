package ch.realmtech.server.craft;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.ecs.component.CraftingTableComponent;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.component.ItemResultCraftComponent;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.item.ItemResultCraftPickEvent;
import ch.realmtech.server.packet.clientPacket.InventorySetPacket;
import com.artemis.ComponentMapper;
import com.artemis.World;

import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public final class OnNewCraftAvailable {
    public static BiFunction<World, Integer, Function<CraftingTableComponent, Consumer<Optional<CraftResult>>>> onCraftAvailableCraftingTable() {
        return (world, entityId) -> {
            ComponentMapper<InventoryComponent> mInventory = world.getMapper(InventoryComponent.class);
            SystemsAdminServer systemsAdminServer = world.getRegistered(SystemsAdminServer.class);
            return (craftingTableComponent) -> {
                return (craftResultOpt) -> {
                    InventoryComponent resultInventoryComponent = mInventory.get(craftingTableComponent.craftingResultInventory);
                    if (craftResultOpt.isEmpty()) {
                        // remove result inventory because no craft is available
                        systemsAdminServer.getInventoryManager().removeInventory(resultInventoryComponent.inventory);
                    } else {
                        CraftResult craftResult = craftResultOpt.get();
                        // add result item to result inventory
                        for (int i = 0; i < craftResult.getResultNumber(); i++) {
                            int nouvelItemResult = systemsAdminServer.getItemManagerServer().newItemInventory(craftResult.getItemResult(), UUID.randomUUID());
                            world.edit(nouvelItemResult).create(ItemResultCraftComponent.class).set(ItemResultCraftPickEvent.removeAllOneItem(craftingTableComponent.craftingInventory));
                            systemsAdminServer.getInventoryManager().addItemToStack(resultInventoryComponent.inventory[0], nouvelItemResult);
                        }
                    }
                    ServerContext serverContext = world.getRegistered("serverContext");
                    UUID craftingResultUuid = systemsAdminServer.getUuidEntityManager().getEntityUuid(craftingTableComponent.craftingResultInventory);

                    serverContext.getServerConnexion().sendPacketToSubscriberForEntity(
                            new InventorySetPacket(craftingResultUuid, serverContext.getSerializerController().getInventorySerializerManager().encode(mInventory.get(craftingTableComponent.craftingResultInventory))),
                            systemsAdminServer.getUuidEntityManager().getEntityUuid(craftingTableComponent.craftingInventory)
                    );
                };
            };
        };
    }

    public static BiFunction<World, Integer, Function<CraftingTableComponent, Consumer<Optional<CraftResult>>>> onCraftAvailableFurnace() {
        return (world, entityId) -> (craftingTableComponent) -> (craftResultOpt) -> craftResultOpt.ifPresent((craftResult) -> {
            SystemsAdminServer systemsAdminServer = world.getRegistered(SystemsAdminServer.class);
            ComponentMapper<InventoryComponent> mInventory = world.getMapper(InventoryComponent.class);

            systemsAdminServer.getInventoryManager().deleteOneItem(systemsAdminServer.getInventoryManager().mInventory.get(craftingTableComponent.craftingInventory).inventory[0]);
            for (int i = 0; i < craftResult.getResultNumber(); i++) {
                int craftResultItemId = systemsAdminServer.getItemManagerServer().newItemInventory(craftResult.getItemResult(), UUID.randomUUID());
                systemsAdminServer.getInventoryManager().addItemToInventory(craftingTableComponent.craftingResultInventory, craftResultItemId);
            }
            ServerContext serverContext = world.getRegistered("serverContext");
            UUID craftingInventoryUuid = systemsAdminServer.getUuidEntityManager().getEntityUuid(craftingTableComponent.craftingInventory);
            UUID craftingResultUuid = systemsAdminServer.getUuidEntityManager().getEntityUuid(craftingTableComponent.craftingResultInventory);

            serverContext.getServerConnexion().sendPacketToSubscriberForEntity(
                    new InventorySetPacket(craftingInventoryUuid, serverContext.getSerializerController().getInventorySerializerManager().encode(mInventory.get(craftingTableComponent.craftingInventory))),
                    systemsAdminServer.getUuidEntityManager().getEntityUuid(entityId)
            );
            serverContext.getServerConnexion().sendPacketToSubscriberForEntity(
                    new InventorySetPacket(craftingResultUuid, serverContext.getSerializerController().getInventorySerializerManager().encode(mInventory.get(craftingTableComponent.craftingResultInventory))),
                    systemsAdminServer.getUuidEntityManager().getEntityUuid(entityId)
            );
        });
    }
}
