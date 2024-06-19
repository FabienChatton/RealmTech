package ch.realmtech.server.craft;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.ecs.component.CraftingTableComponent;
import ch.realmtech.server.ecs.component.FurnaceComponent;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.component.ItemComponent;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.packet.clientPacket.FurnaceExtraInfoPacket;
import com.artemis.ComponentMapper;
import com.artemis.World;

import java.util.Optional;

public class CraftChangeFunctions {
    public static CraftChange craftResultChangeCraftingTable(World world) {
        return (craftingTableId) -> {
            SystemsAdminServer systemsAdminServer = world.getRegistered(SystemsAdminServer.class);
            ComponentMapper<CraftingTableComponent> mCraftingTable = world.getMapper(CraftingTableComponent.class);
            CraftingTableComponent craftingTableComponent = mCraftingTable.get(craftingTableId);
            InventoryComponent craftingInventoryComponent = systemsAdminServer.getInventoryManager().mInventory.get(craftingTableComponent.craftingInventory);
            InventoryComponent craftingResultInventoryComponent = systemsAdminServer.getInventoryManager().mInventory.get(craftingTableComponent.craftingResultInventory);

            Optional<CraftResult> craftResult = systemsAdminServer.getCraftingManager().getNewCraftResult(craftingTableComponent);

            boolean canProcessCraft = false;
            ItemComponent itemDejaResultComponent = systemsAdminServer.getInventoryManager().mItem.get(craftingResultInventoryComponent.inventory[0][0]);
            if (craftResult.isPresent()) {
                // new craft available
                if (itemDejaResultComponent == null) {
                    canProcessCraft = true;
                } else if (itemDejaResultComponent.itemRegisterEntry != craftResult.get().getItemResult()) {
                    canProcessCraft = true;
                }
            } else {
                // craft to remove
                if (itemDejaResultComponent != null) {
                    canProcessCraft = true;
                }
            }
            if (canProcessCraft) {
                return Optional.of(craftResult);
            } else {
                return Optional.empty();
            }
        };
    }

    public static CraftChange craftResultChangeFurnace(World world) {
        return (furnaceId) -> {
            SystemsAdminServer systemsAdminServer = world.getRegistered(SystemsAdminServer.class);
            ComponentMapper<CraftingTableComponent> mCraftingTable = world.getMapper(CraftingTableComponent.class);
            ComponentMapper<FurnaceComponent> mFurnace = world.getMapper(FurnaceComponent.class);
            ComponentMapper<InventoryComponent> mInventory = world.getMapper(InventoryComponent.class);
            ComponentMapper<ItemComponent> mItem = world.getMapper(ItemComponent.class);

            CraftingTableComponent craftingTableComponent = mCraftingTable.get(furnaceId);
            FurnaceComponent furnaceComponent = mFurnace.get(furnaceId);
            InventoryComponent craftingResultInventoryComponent = mInventory.get(craftingTableComponent.craftingResultInventory);

            Optional<CraftResult> craftResultOpt = systemsAdminServer.getCraftingManager().getNewCraftResult(craftingTableComponent);

            // do absolutely nothing if no carburant in the furnace
            if (furnaceComponent.remainingTickToBurn <= 0) {
                furnaceComponent.tickProcess = 0;
                return Optional.empty();
            }

            // no craft available
            if (craftResultOpt.isEmpty()) {
                furnaceComponent.tickProcess = 0;
                return Optional.empty();
            }

            CraftResult craftResult = craftResultOpt.get();
            ItemComponent itemWitness = mItem.get(craftingResultInventoryComponent.inventory[0][0]);

            // only if item in result is the same as the craft
            if (itemWitness != null) {
                if (itemWitness.itemRegisterEntry != craftResult.getItemResult()) {
                    furnaceComponent.tickProcess = 0;
                    return Optional.empty();
                }
            }

            furnaceComponent.tickProcess++;

            // craft available and process has finished
            if (furnaceComponent.tickProcess >= craftResult.getTimeToProcess()) {
                furnaceComponent.tickProcess = 0;
                return Optional.of(craftResultOpt);
            }

            // sync to player for icon
            if (furnaceComponent.tickProcess == 1) {
                ServerContext serverContext = world.getRegistered("serverContext");
                serverContext.getServerConnexion().sendPacketToSubscriberForEntity(new FurnaceExtraInfoPacket(
                                systemsAdminServer.getUuidEntityManager().getEntityUuid(furnaceId),
                                -1,
                                craftResult.getTimeToProcess()),
                        systemsAdminServer.getUuidEntityManager().getEntityUuid(furnaceId));
            }

            return Optional.empty();
        };
    }
}
