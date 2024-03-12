package ch.realmtech.server.craft;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.ecs.component.CraftingTableComponent;
import ch.realmtech.server.ecs.component.FurnaceComponent;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.component.ItemComponent;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.ecs.system.CraftingManager;
import ch.realmtech.server.newCraft.NewCraftResult;
import ch.realmtech.server.packet.clientPacket.FurnaceExtraInfoPacket;
import com.artemis.ComponentMapper;
import com.artemis.World;

import java.util.Optional;
import java.util.function.Function;

public final class CraftResultChangeFunction {
    public static Function<Integer, Optional<CraftResultChange>> CraftResultChangeCraftingTable(World world) {
        return (craftingTableId) -> {
            SystemsAdminServer systemsAdminServer = world.getRegistered(SystemsAdminServer.class);
            ComponentMapper<CraftingTableComponent> mCraftingTable = world.getMapper(CraftingTableComponent.class);
            CraftingTableComponent craftingTableComponent = mCraftingTable.get(craftingTableId);
            InventoryComponent craftingInventoryComponent = systemsAdminServer.inventoryManager.mInventory.get(craftingTableComponent.craftingInventory);
            InventoryComponent craftingResultInventoryComponent = systemsAdminServer.inventoryManager.mInventory.get(craftingTableComponent.craftingResultInventory);

            Optional<NewCraftResult> craftResult = world.getSystem(CraftingManager.class).getNewCraftResult(craftingTableComponent);

            boolean canProcessCraft = false;
            ItemComponent itemDejaResultComponent = systemsAdminServer.inventoryManager.mItem.get(craftingResultInventoryComponent.inventory[0][0]);
            if (craftResult.isPresent()) {
                // new craft available
                if (itemDejaResultComponent == null) {
                    canProcessCraft = true;
                }
            } else {
                // craft to remove
                if (itemDejaResultComponent != null) {
                    canProcessCraft = true;
                }
            }
            if (canProcessCraft) {
                return Optional.of(new CraftResultChange(craftResult));
            } else {
                return Optional.empty();
            }
        };
    }

    public static Function<Integer, Optional<CraftResultChange>> CraftResultChangeFurnace(World world) {
        return (furnaceId) -> {
            SystemsAdminServer systemsAdminServer = world.getRegistered(SystemsAdminServer.class);
            ComponentMapper<CraftingTableComponent> mCraftingTable = world.getMapper(CraftingTableComponent.class);
            ComponentMapper<FurnaceComponent> mFurnace = world.getMapper(FurnaceComponent.class);
            ComponentMapper<InventoryComponent> mInventory = world.getMapper(InventoryComponent.class);
            ComponentMapper<ItemComponent> mItem = world.getMapper(ItemComponent.class);

            CraftingTableComponent craftingTableComponent = mCraftingTable.get(furnaceId);
            FurnaceComponent furnaceComponent = mFurnace.get(furnaceId);
            InventoryComponent craftingInventoryComponent = mInventory.get(craftingTableComponent.craftingInventory);
            InventoryComponent carburantInventoryComponent = mInventory.get(furnaceComponent.inventoryCarburant);

            Optional<NewCraftResult> craftResultOpt = world.getSystem(CraftingManager.class).getNewCraftResult(craftingTableComponent);

            if (craftResultOpt.isPresent()) {
                NewCraftResult craftResult = craftResultOpt.get();
                if (furnaceComponent.tickProcess >= craftResult.getTimeToProcess()) {
                    furnaceComponent.tickProcess = 0;
                    return Optional.of(new CraftResultChange(craftResultOpt));
                } else {
                    if (furnaceComponent.remainingTickToBurn > 0) {
                        furnaceComponent.tickProcess++;

                        // sync to player for icon
                        if (furnaceComponent.tickProcess == 1) {
                            ServerContext serverContext = world.getRegistered("serverContext");
                            serverContext.getServerConnexion().sendPacketToSubscriberForEntity(new FurnaceExtraInfoPacket(
                                            systemsAdminServer.uuidEntityManager.getEntityUuid(furnaceId),
                                    -1,
                                    craftResult.getTimeToProcess())
                                    , systemsAdminServer.uuidEntityManager.getEntityUuid(furnaceId));
                        }
                    }
                }
            } else {
                furnaceComponent.tickProcess = 0;
            }

            return Optional.empty();
        };
    }
}
