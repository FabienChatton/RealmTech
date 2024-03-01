package ch.realmtech.server.craft;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.ecs.component.CraftingTableComponent;
import ch.realmtech.server.ecs.component.FurnaceComponent;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.component.ItemComponent;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.ecs.system.CraftingManager;
import ch.realmtech.server.packet.clientPacket.FurnaceExtraInfoPacket;
import ch.realmtech.server.registery.ItemRegisterEntry;
import com.artemis.ComponentMapper;
import com.artemis.World;

import java.util.Arrays;
import java.util.List;
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

            Optional<CraftResult> craftResult = world.getSystem(CraftingManager.class).getCraftResult(craftingTableComponent);

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

            Optional<CraftResult> craftResultOpt = world.getSystem(CraftingManager.class).getCraftResult(craftingTableComponent);

            if (craftResultOpt.isPresent()) {
                CraftResult craftResult = craftResultOpt.get();
                if (furnaceComponent.tickProcess >= craftResult.getTimeToProcess()) {
                    furnaceComponent.tickProcess = 0;
                    return Optional.of(new CraftResultChange(craftResultOpt));
                } else {
                    if (furnaceComponent.remainingTickToBurn > 0) {
                        furnaceComponent.tickProcess++;

                        // sync to player for icon
                        if (furnaceComponent.tickProcess == 1) {
                            ServerContext serverContext = world.getRegistered("serverContext");
                            serverContext.getServerConnexion().sendPacketToSubscriberForEntityId(new FurnaceExtraInfoPacket(
                                            systemsAdminServer.uuidEntityManager.getEntityUuid(furnaceId),
                                    -1,
                                    craftResult.getTimeToProcess())
                                    , furnaceId);
                        }
                    }
                }
            } else {
                furnaceComponent.tickProcess = 0;
            }

            return Optional.empty();
        };
    }

    public static Optional<CraftResult> getCraftResult(SystemsAdminServer systemsAdminServer, CraftingTableComponent craftingTableComponent, InventoryComponent craftingInventoryComponent) {
        List<ItemRegisterEntry> itemRegisterEntries = Arrays.stream(craftingInventoryComponent.inventory)
                .map((stack) -> systemsAdminServer.inventoryManager.mItem.get(stack[0]))
                .map((itemComponent) -> itemComponent != null ? itemComponent.itemRegisterEntry : null)
                .toList();

        List<CraftResult> craftResults = craftingTableComponent.getRegistry().getEnfants().stream()
                .map((craftingRecipe) -> craftingRecipe.getEntry().craft(itemRegisterEntries))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        Optional<CraftResult> craftResult;
        if (!craftResults.isEmpty()) {
            craftResult = Optional.of(craftResults.get(0));
        } else {
            craftResult = Optional.empty();
        }
        return craftResult;
    }
}
