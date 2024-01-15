package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.ctrl.ItemManager;
import ch.realmtech.server.ecs.component.CraftingTableComponent;
import ch.realmtech.server.ecs.component.FurnaceComponent;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.component.ItemComponent;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.mod.RealmTechCoreMod;
import ch.realmtech.server.packet.clientPacket.InventorySetPacket;
import ch.realmtech.server.registery.ItemRegisterEntry;
import ch.realmtech.server.registery.RegistryEntry;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;

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
    private ComponentMapper<ItemComponent> mItem;
    private ComponentMapper<InventoryComponent> mInventory;

    @Override
    protected void process(int entityId) {
        FurnaceComponent furnaceComponent = mFurnace.get(entityId);
        InventoryComponent carburantInventoryComponent = mInventory.get(furnaceComponent.inventoryCarburant);

        // à mettre tout le changement d'état autre par, comme dans "furnaceProcess"
        if (furnaceComponent.remainingTickToBurn > 0) {
            furnaceComponent.remainingTickToBurn--;
        }

        // met à jour le carburant
        if (furnaceComponent.remainingTickToBurn <= 0) {
            int[] carburantStack = carburantInventoryComponent.inventory[0];
            int carburantItemId = systemsAdminServer.inventoryManager.getTopItem(carburantStack);

            ItemComponent carburantItemComponent = mItem.get(carburantItemId);
            if (carburantItemComponent != null) {
                if (carburantItemComponent.itemRegisterEntry.getItemBehavior().getTimeToBurn() > 0) {
                    furnaceComponent.remainingTickToBurn = carburantItemComponent.itemRegisterEntry.getItemBehavior().getTimeToBurn();
                    systemsAdminServer.inventoryManager.deleteItemInStack(carburantStack, carburantItemId);

                    UUID carburantInventoryUuid = systemsAdminServer.uuidComponentManager.getRegisteredComponent(furnaceComponent.inventoryCarburant).getUuid();
                    serverContext.getServerHandler().broadCastPacket(new InventorySetPacket(carburantInventoryUuid, serverContext.getSerializerController().getInventorySerializerManager().encode(carburantInventoryComponent)));
                }
            }
        }
//        FurnaceComponent furnaceComponent = mFurnace.get(entityId);
//        InventoryComponent iconInventoryTimeToBurnComponent = mInventory.get(furnaceComponent.iconInventoryTimeToBurn);
//        InventoryComponent iconInventoryCurentBurnTimeComponent = mInventory.get(furnaceComponent.iconInventoryCurentBurnTime);
//        ItemComponent itemIconTimeToBurnComponent = mItem.get(iconInventoryTimeToBurnComponent.inventory[0][0]);
//
//
//        if (furnaceComponent.curentBurnTime > 0) {
//            int pourDix = 10 * furnaceComponent.curentBurnTime / furnaceComponent.itemBurn.getItemBehavior().getTimeToBurn();
//            if (pourDix == 0) pourDix = 1;
//            String format;
//            if (pourDix >= 10) {
//                format = "furnace-arrow-10";
//            } else if (pourDix <= 0) {
//                format = "furnace-arrow-01";
//            } else {
//                format = String.format("furnace-arrow-0%d", pourDix);
//            }
//            ItemRegisterEntry iconTimeToBurn = RealmTechCoreMod.ITEMS.getEnfants().stream()
//                    .filter(itemRegisterEntryRegistryEntry -> itemRegisterEntryRegistryEntry.getEntry().getTextureRegionName()
//                            .equals(format))
//                    .map(RegistryEntry::getEntry)
//                    .findFirst()
//                    .orElse(RealmTechCoreMod.NO_ITEM);
//            if (iconTimeToBurn == RealmTechCoreMod.NO_ITEM) {
//                System.out.println();
//            }
//            if (itemIconTimeToBurnComponent == null || itemIconTimeToBurnComponent.itemRegisterEntry != iconTimeToBurn) {
//                world.getSystem(InventoryManager.class).removeInventory(iconInventoryCurentBurnTimeComponent.inventory);
//                int iconId = itemManager.newItemInventory(iconTimeToBurn, UUID.randomUUID());
//                world.getSystem(InventoryManager.class).addItemToStack(iconInventoryCurentBurnTimeComponent.inventory[0], iconId);
//            }
//        } else {
//            if (!mItem.has(iconInventoryCurentBurnTimeComponent.inventory[0][0]) || mItem.get(iconInventoryCurentBurnTimeComponent.inventory[0][0]).itemRegisterEntry != RealmTechCoreMod.ICON_FURNACE_TIME_TO_BURN_01) {
//                world.getSystem(InventoryManager.class).removeInventory(iconInventoryCurentBurnTimeComponent.inventory);
//                int iconId = itemManager.newItemInventory(RealmTechCoreMod.ICON_FURNACE_ARROW_01, UUID.randomUUID());
//                world.getSystem(InventoryManager.class).addItemToStack(iconInventoryCurentBurnTimeComponent.inventory[0], iconId);
//            }
//        }
//
//        if (furnaceComponent.timeToBurn > 0) {
//            int pourDix = 10 * furnaceComponent.timeToBurn / furnaceComponent.itemBurn.getItemBehavior().getTimeToBurn();
//            if (pourDix == 0) pourDix = 1;
//            String format;
//            if (pourDix >= 10) {
//                format = "furnace-time-to-burn-10";
//            } else {
//                format = String.format("furnace-time-to-burn-0%d", pourDix);
//            }
//            ItemRegisterEntry iconTimeToBurn = RealmTechCoreMod.ITEMS.getEnfants().stream()
//                    .filter(itemRegisterEntryRegistryEntry -> itemRegisterEntryRegistryEntry.getEntry().getTextureRegionName()
//                            .equals(format))
//                    .map(RegistryEntry::getEntry)
//                    .findFirst()
//                    .orElse(RealmTechCoreMod.NO_ITEM);
//            if (itemIconTimeToBurnComponent == null || itemIconTimeToBurnComponent.itemRegisterEntry != iconTimeToBurn) {
//                world.getSystem(InventoryManager.class).removeInventory(iconInventoryTimeToBurnComponent.inventory);
//                int iconId = itemManager.newItemInventory(iconTimeToBurn, UUID.randomUUID());
//                world.getSystem(InventoryManager.class).addItemToStack(iconInventoryTimeToBurnComponent.inventory[0], iconId);
//            }
//        } else {
//            if (!mItem.has(iconInventoryTimeToBurnComponent.inventory[0][0]) || mItem.get(iconInventoryTimeToBurnComponent.inventory[0][0]).itemRegisterEntry != RealmTechCoreMod.ICON_FURNACE_TIME_TO_BURN_01) {
//                world.getSystem(InventoryManager.class).removeInventory(iconInventoryTimeToBurnComponent.inventory);
//                int iconId = itemManager.newItemInventory(RealmTechCoreMod.ICON_FURNACE_TIME_TO_BURN_01, UUID.randomUUID());
//                world.getSystem(InventoryManager.class).addItemToStack(iconInventoryTimeToBurnComponent.inventory[0], iconId);
//            }
//        }
    }

//    private void afficheIcon(int ref, int max, String baseFormat, InventoryComponent iconInventory, ItemComponent iconItem, boolean inverse) {
//        if (ref > 0) {
//            int pourDix = 10 * ref / max;
//            String format;
//            if (pourDix >= 10) {
//                format = baseFormat + "-10";
//            } else {
//                format = String.format(baseFormat + "-0%d", pourDix);
//            }
//            ItemRegisterEntry iconTimeToBurn = RealmTechCoreMod.ITEMS.getEnfants().stream()
//                    .filter(itemRegisterEntryRegistryEntry -> itemRegisterEntryRegistryEntry.getEntry().getTextureRegionName()
//                            .equals(format))
//                    .map(RegistryEntry::getEntry)
//                    .findFirst()
//                    .orElse(RealmTechCoreMod.NO_ITEM);
//            if (iconItem == null || iconItem.itemRegisterEntry != iconTimeToBurn) {
//                world.getSystem(InventoryManager.class).removeInventory(iconInventory.inventory);
//                int iconId = itemManager.newItemInventory(iconTimeToBurn, UUID.randomUUID());
//                world.getSystem(InventoryManager.class).addItemToStack(iconInventory.inventory[0], iconId);
//            }
//        } else {
//            if (!mItem.has(iconInventory.inventory[0][0]) || mItem.get(iconInventory.inventory[0][0]).itemRegisterEntry != RealmTechCoreMod.ICON_FURNACE_TIME_TO_BURN_01) {
//                world.getSystem(InventoryManager.class).removeInventory(iconInventory.inventory);
//                int iconId = itemManager.newItemInventory(RealmTechCoreMod.ICON_FURNACE_TIME_TO_BURN_01, UUID.randomUUID());
//                world.getSystem(InventoryManager.class).addItemToStack(iconInventory.inventory[0], iconId);
//            }
//        }
//    }
}
