package ch.realmtech.server.ecs.system;

import ch.realmtech.server.craft.CraftChangeFunctions;
import ch.realmtech.server.craft.OnNewCraftAvailable;
import ch.realmtech.server.ctrl.ItemManager;
import ch.realmtech.server.ecs.component.*;
import ch.realmtech.server.ecs.plugin.commun.ContextType;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import ch.realmtech.server.registry.*;
import ch.realmtech.server.serialize.SerializerController;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.EntityEdit;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.artemis.utils.IntBag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class InventoryManager extends Manager {
    private final static Logger logger = LoggerFactory.getLogger(InventoryManager.class);
    @Wire(name = "systemsAdmin")
    private SystemsAdminCommun systemsAdminCommun;
    @Wire(name = "rootRegistry")
    private Registry<?> rootRegistry;
    @Wire
    private ItemManager itemManager;
    public ComponentMapper<InventoryComponent> mInventory;
    public ComponentMapper<ItemComponent> mItem;
    private ComponentMapper<TextureComponent> mTexture;
    private ComponentMapper<ChestComponent> mChest;
    private ComponentMapper<InventoryCursorComponent> mCursor;
    private ComponentMapper<CraftingTableComponent> mCraftingTable;
    private ComponentMapper<ItemResultCraftComponent> mItemResult;
    private ComponentMapper<InventoryUiComponent> mInventoryUi;
    private ComponentMapper<FurnaceComponent> mFurnace;

    public boolean addItemToInventory(int inventoryId, int itemId) {
        return addItemToInventory(mInventory.get(inventoryId), itemId);
    }

    /**
     * Parcourt l'inventaire à la recherche d'un emplacement disponible pour ajouter l'item
     *
     * @param inventoryComponent L'entité où l'item sera ajouté.
     * @param itemId             l'item souhaité a ajouter.
     * @return vrai si l'item a été ajouté avec success.
     */
    public boolean addItemToInventory(InventoryComponent inventoryComponent, int itemId) {
        for (int i = 0; i < inventoryComponent.inventory.length; i++) {
            if (addItemToStack(inventoryComponent.inventory[i], itemId)) {
                return true;
            }
        }
        return false;
    }

    public boolean moveStackToStackNumber(int[] stackSrc, int[] stackDst, int desiredNumber) {
        int max = stackSrc.length - tailleStack(stackDst);
        int available = tailleStack(stackSrc);

        int n = Math.min(Math.min(available, max), desiredNumber);

        if (n <= 0) return false;

        for (int i = available - 1, j = 0; i >= 0 && j < n; i--, j++) {
            if (!addItemToStack(stackDst, stackSrc[i])) {
                return false;
            } else {
                stackSrc[i] = 0;
            }
        }

        return true;
    }

    public boolean addItemToStack(int[] stack, int itemId) {
        // pas d'item dans le stack alors ajout
        if (stack[0] == 0) {
            stack[0] = itemId;
            return true;
        }

        final ItemComponent inventoryItemComponent = mItem.get(stack[0]);
        final ItemComponent itemComponent = mItem.get(itemId);
        if (!(inventoryItemComponent.itemRegisterEntry == itemComponent.itemRegisterEntry)) {
            return false; // pas le même registre, le stack doit toujours être pour un même registre
        }

        for (int i = 0; i < stack.length; i++) {
            if (stack[i] == 0) {
                stack[i] = itemId;
                return true; // Il y a dans la stack un emplacement vide
            }
        }
        return false; // Il n'y pas d'emplacement dans la stack pour ajouter l'item
    }

    /**
     * Donne la taille de la stack. C'est-à-dire que les emplacements vides sont ignorés
     * @param stack la stack à compter.
     * @return la taille de la stack avec des emplacements remplie.
     */
    public static int tailleStack(int[] stack) {
        int count = 0;
        for (int i = 0; i < stack.length; i++) {
            if (stack[i] != 0) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    public int[][] getInventory(int entityId) {
        return mInventory.get(entityId).inventory;
    }

    public static void clearStack(int[] stack) {
        Arrays.fill(stack, 0);
    }

    /**
     * Remove all items in this inventory from this world and set to 0 all slot in all stack.
     * @param inventory The inventory to remove.
     */
    public void removeInventory(int[][] inventory) {
        for (int[] stack : inventory) {
            deleteStack(stack);
        }
    }

    public void removeInventoryUi(int inventoryId) {
        InventoryComponent inventoryComponent = mInventory.get(inventoryId);
        removeInventory(inventoryComponent.inventory);
        systemsAdminCommun.getUuidEntityManager().deleteRegisteredEntity(inventoryId);
    }

    /**
     * Delete all items from in this stack from this world and set to 0 all slot.
     * @param stack The stack to remove.
     */
    public void deleteStack(int[] stack) {
        final int tailleStack = InventoryManager.tailleStack(stack);
        for (int i = 0; i < tailleStack; i++) {
            world.delete(stack[i]);
            systemsAdminCommun.getUuidEntityManager().deleteRegisteredEntity(stack[i]);
        }
        clearStack(stack);
    }

    /**
     * Delete the item on top of this stack.
     * @param stack The stack to remove.
     */
    public void deleteOneItem(int[] stack) {
        int taille = tailleStack(stack);
        if (taille == 0) return;
        world.delete(stack[taille - 1]);
        stack[taille - 1] = 0;
    }

    /**
     * Delete all item on top of this inventory
     * @param inventory The inventory to remove top item.
     */
    public void deleteAllOneItem(int[][] inventory) {
        for (int i = 0; i < inventory.length; i++) {
            deleteOneItem(inventory[i]);
        }
    }

    /**
     * For the stack to be moved, the src stack must not be empty and the items
     * in the source stack and the dst stack must be in the same register.
     * @param src The source stack.
     * @param dst The destination stack.
     */
    public boolean canMouveStack(int[] src, int[] dst) {
        if (src[0] == 0) return false;
        if (dst[0] == 0) return true;
        ItemComponent itemComponentSrc = mItem.get(src[0]);
        ItemComponent itemComponentDst = mItem.get(dst[0]);
        return dst.length - tailleStack(dst) >= tailleStack(src) && itemComponentSrc.itemRegisterEntry == itemComponentDst.itemRegisterEntry;
    }

    public int getTopItem(int[] stack) {
        int tailleStack = tailleStack(stack);
        if (tailleStack > 0) {
            return stack[tailleStack - 1];
        } else {
            return 0;
        }
    }

    /**
     * Get the stack that contains this item id.
     * @param itemId The item id where is the stack to find.
     * @param inventoryId The inventory to search into.
     * @return The stack that contains the item id or null if stack was not found.
     */
    public int[] getStackContainsItem(int inventoryId, int itemId) {
        InventoryComponent inventoryComponent = mInventory.get(inventoryId);
        for (int[] stack : inventoryComponent.inventory) {
            for (int i : stack) {
                if (i == itemId) {
                    return stack;
                }
            }
        }
        return null;
    }

    /**
     * Delete a item in this inventory. This item can be anywhere in this inventory.
     * <strong>Delete</string> this item from this world.
     * @param inventoryId The inventory where the item will be remove.
     * @param itemId The item id to remove.
     * @return true if the operation was successful.
     */
    public boolean deleteItemInInventory(int inventoryId, int itemId) {
        int[] stack = getStackContainsItem(inventoryId, itemId);
        if (stack == null) return false;
        return deleteItemInStack(stack, itemId);
    }

    /**
     * Remove a item in this inventory. This item can be anywhere in this inventory.
     * <strong>Do not delete</strong> this item from this world.
     * @param inventoryId The inventory where the item will be remove.
     * @param itemId The item id to remove.
     * @return true if the operation was successful.
     */
    public boolean removeItemInInventory(int inventoryId, int itemId) {
        int[] stack = getStackContainsItem(inventoryId, itemId);
        if (stack == null) return false;
        return removeItemInStack(stack, itemId);
    }

    /**
     * Delete one item in this stack. The item can be anywhere in this stack.
     * <strong>Delete</string> this item from this world.
     * @param stack  The stack the contains the item.
     * @param itemId The item id to remove.
     * @return true if the operation was successful.
     */
    public boolean deleteItemInStack(int[] stack, int itemId) {
        if (removeItemInStack(stack, itemId)) {
            world.delete(itemId);
            return true;
        } else  {
            return false;
        }
    }

    /**
     * Remove one item in this stack. The item can be anywhere in this stack.
     * <strong>Do not delete</strong> the item from this world. See non static
     * method {@link #deleteItemInStack} for delete item.
     * @param stack  The stack the contains the item.
     * @param itemId The item id to remove.
     * @return true if the operation was successful.
     */
    public static boolean removeItemInStack(int[] stack, int itemId) {
        int index = -1;
        for (int i = 0; i < stack.length; i++) {
            if (stack[i] == itemId) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            return false;
        }

        int[] resultArray = new int[stack.length];
        for (int i = 0, j = 0; j < stack.length; i++, j++) {
            if (i == index) {
                j++;
            }
            resultArray[i] = stack[j];
        }
        System.arraycopy(resultArray, 0, stack, 0, stack.length);
        return true;
    }

    /**
     * Give the {@link InventoryComponent} id who has this {@link UUID}.
     * @param uuid The uuid value to test with
     * @return The corresponding inventory id or -1 if none inventory has this uuid value.
     */
    public int getInventoryByUUID(UUID uuid) {
        return systemsAdminCommun.getUuidEntityManager().getEntityId(uuid);
    }

    /**
     * Get entity with a inventory component who this inventory has this stack
     * @param srcStack The stack to search with inventory
     * @return the entity id who as this stack or -1 if not found.
     */
    public int getInventoryByStack(int[] srcStack) {
        IntBag inventoryEntities = world.getAspectSubscriptionManager().get(Aspect.all(InventoryComponent.class)).getEntities();
        int[] inventoryData = inventoryEntities.getData();
        for (int i = 0; i < inventoryEntities.size(); i++) {
            int inventoryId = inventoryData[i];
            InventoryComponent inventoryComponent = mInventory.get(inventoryId);
            int[][] inventory = inventoryComponent.inventory;
            for (int[] stack : inventory) {
                if (stack == srcStack) {
                    return inventoryId;
                }
            }
        }
        return -1;
    }

    /**
     * Give the {@link InventoryComponent} who has this {@link UUID}.
     * @param uuid The uuid value to test with
     * @return The corresponding inventory id or null if none inventory has this {@link UUID}.
     */
    public InventoryComponent getInventoryComponentByUUID(UUID uuid) {
        int inventoryId = getInventoryByUUID(uuid);
        return inventoryId != -1 ? mInventory.get(inventoryId) : null;
    }

    public int getItemInInventoryByUuid(InventoryComponent inventoryComponent, UUID itemUuidToFind) {
        if (itemUuidToFind == null) return -1;
        for (int i = 0; i < inventoryComponent.inventory.length; i++) {
            for (int j = 0; j < inventoryComponent.inventory[i].length; j++) {
                int itemId = inventoryComponent.inventory[i][j];
                UUID itemUuid = systemsAdminCommun.getUuidEntityManager().getEntityUuid(itemId);
                if (itemUuid != null) {
                    if (itemUuid.equals(itemUuidToFind)) {
                        return itemId;
                    }

                }
            }
        }
        return -1;
    }

    public int[] moveStackToStackRequest(UUID srcInventoryUUID, UUID dstInventoryUUID, UUID[] itemsToMove, int slotIndex) throws IllegalAccessError {
        int srcInventoryId = getInventoryByUUID(srcInventoryUUID);
        int dstInventoryId = getInventoryByUUID(dstInventoryUUID);
        if (srcInventoryId == -1) {
            throw new IllegalAccessError("The src inventory: " + srcInventoryId + "  was not found. Uuid: " + srcInventoryUUID);
        }

        if (dstInventoryId == -1) {
            throw new IllegalAccessError("The dst inventory: " + srcInventoryId + "  was not found. Uuid " + dstInventoryUUID);
        }

        int[] itemsSrcId = new int[itemsToMove.length];
        for (int i = 0; i < itemsToMove.length; i++) {
            UUID itemUuid = itemsToMove[i];
            int itemId = systemsAdminCommun.getUuidEntityManager().getEntityId(itemUuid);
            itemsSrcId[i] = itemId;
            if (itemId == -1) {
                throw new IllegalAccessError("The item id: " + itemUuid + "  was not found");
            }
        }

        int[] srcStack = null;
        for (int i = 0; i < itemsSrcId.length; i++) {
            int[] stack = getStackContainsItem(srcInventoryId, itemsSrcId[i]);
            if (srcStack == null) {
                srcStack = stack;
            } else {
                if (stack != srcStack) {
                    throw new IllegalAccessError("Items are split between multiple stack");
                }
            }
        }

        if (srcStack == null) {
            throw new IllegalAccessError("Source can't be null");
        }

        int[][] dstInventory = mInventory.get(dstInventoryId).inventory;
        if (slotIndex > dstInventory.length) {
            throw new IllegalAccessError("slot item is out of bound");
        }

        int[] mutatedInventories = null;
        int[] dstStack = dstInventory[slotIndex];

        if (tailleStack(dstStack) < dstStack.length) {
            int itemCraftResultTemoin = srcStack[0];
            if (mItemResult.has(itemCraftResultTemoin)) {
                if (itemsToMove.length < tailleStack(srcStack)) throw new IllegalAccessError("The craft result can only be fully moved");
                ItemResultCraftComponent itemResultCraftComponent = mItemResult.get(itemCraftResultTemoin);
                mutatedInventories = itemResultCraftComponent.pickEvent.pick(world);
                for (int i = 0; i < InventoryManager.tailleStack(srcStack); i++) {
                    world.edit(srcStack[i]).remove(ItemResultCraftComponent.class);
                }
            }

            boolean inventoryMoved = moveStackToStackNumber(srcStack, dstStack, itemsToMove.length);
            if (!inventoryMoved) throw new IllegalAccessError("move stack to stack has no effect");
            return mutatedInventories;
        } else {
            throw new IllegalAccessError("No inventory change");
        }
    }

    public int createCursorInventory(int motherEntity, UUID inventoryUuid, int numberOfSlotParRow, int numberOfRow) {
        int inventoryId = world.create();
        world.edit(motherEntity).create(InventoryCursorComponent.class).set(inventoryId);

        EntityEdit inventoryEdit = world.edit(inventoryId);
        systemsAdminCommun.getUuidEntityManager().registerEntityIdWithUuid(inventoryUuid, inventoryId);
        inventoryEdit.create(InventoryComponent.class).set(numberOfSlotParRow, numberOfRow);
        return inventoryId;
    }

    public int createChest(int motherEntity, UUID inventoryUuid, int numberOfSlotParRow, int numberOfRow) {
        return createChest(motherEntity, new int[numberOfSlotParRow * numberOfRow][InventoryComponent.DEFAULT_STACK_LIMITE], inventoryUuid, numberOfSlotParRow, numberOfRow);
    }

    public int createChest(int motherEntity, int[][] inventory, UUID inventoryUuid, int numberOfSlotParRow, int numberOfRow) {
        int inventoryId = world.create();
        world.edit(motherEntity).create(ChestComponent.class).set(inventoryId);
        systemsAdminCommun.getCellPaddingManager().addOrCreate(motherEntity, world.getRegistered(SerializerController.class).getChestSerializerController());

        createInventoryUi(inventoryId, inventoryUuid, inventory, numberOfSlotParRow, numberOfRow);
        return inventoryId;
    }

    /** @return a table with the first index of crafting inventoryId and the seconde index of crafting result inventoryId */
    public int[] createCraftingTable(int motherEntity, UUID craftingInventoryUuid, int craftingNumberOfSlotParRow, int craftingNumberOfRow, UUID craftingResultInventoryUuid) {
        String tagQuery = "#craftingTableRecipes";
        List<? extends CraftRecipeEntry> craftRecipes = RegistryUtils.findEntries(rootRegistry, tagQuery);
        return createCraftingTable(motherEntity, craftingInventoryUuid, new int[craftingNumberOfSlotParRow * craftingNumberOfRow][InventoryComponent.DEFAULT_STACK_LIMITE], craftingNumberOfSlotParRow, craftingNumberOfRow, craftingResultInventoryUuid, craftRecipes);
    }

    public int[] createCraftingTable(int motherEntity, UUID craftingInventoryUuid, int[][] craftingInventory, int craftingNumberOfSlotParRow, int craftingNumberOfRow, UUID craftingResultInventoryUuid, List<? extends CraftRecipeEntry> craftRecipes) {
        int craftingInventoryId = world.create();
        int craftingResultInventoryId = world.create();

        world.edit(motherEntity).create(CraftingTableComponent.class).set(craftingInventoryId, craftingResultInventoryId, craftRecipes, CraftChangeFunctions.craftResultChangeCraftingTable(world), OnNewCraftAvailable.onCraftAvailableCraftingTable());
        systemsAdminCommun.getCellPaddingManager().addOrCreate(motherEntity, world.getRegistered(SerializerController.class).getCraftingTableController());
        EntityEdit craftingInventoryEdit = world.edit(craftingInventoryId);
        systemsAdminCommun.getUuidEntityManager().registerEntityIdWithUuid(craftingInventoryUuid, craftingInventoryId);
        craftingInventoryEdit.create(InventoryComponent.class).set(craftingInventory, craftingNumberOfSlotParRow, craftingNumberOfRow);
        systemsAdminCommun.onContextType(ContextType.CLIENT, () -> craftingInventoryEdit.create(InventoryUiComponent.class).set());

        createInventoryUi(craftingResultInventoryId, craftingResultInventoryUuid, 1,1);
        return new int[]{craftingInventoryId, craftingResultInventoryId};
    }

    public int[] createFurnace(int motherEntity, UUID furnaceUuid, UUID craftingInventoryUuid, int[][] craftingInventory, UUID carburantInventoryUuid, int[][] carburantInventory, UUID craftingResultInventoryUuid, int[][] craftingResultInventory) {
        int craftingInventoryId = world.create();
        int craftingResultInventoryId = world.create();
        int carburantInventoryId = world.create();

        createInventoryUi(craftingInventoryId, craftingInventoryUuid, craftingInventory, 1, 1);
        createInventoryUi(craftingResultInventoryId, craftingResultInventoryUuid,  craftingResultInventory, 1, 1);
        createInventoryUi(carburantInventoryId, carburantInventoryUuid, carburantInventory, 1, 1);


        List<? extends FurnaceCraftShapeless> furnaceRecipes = RegistryUtils.findEntries(systemsAdminCommun.getRootRegistry(), "#furnaceRecipes");
        world.edit(motherEntity).create(CraftingTableComponent.class).set(craftingInventoryId, craftingResultInventoryId, furnaceRecipes, CraftChangeFunctions.craftResultChangeFurnace(world), OnNewCraftAvailable.onCraftAvailableFurnace());
        world.edit(motherEntity).create(FurnaceComponent.class).set(carburantInventoryId);
        systemsAdminCommun.getCellPaddingManager().addOrCreate(motherEntity, world.getRegistered(SerializerController.class).getFurnaceSerializerController());

        systemsAdminCommun.getUuidEntityManager().registerEntityIdWithUuid(furnaceUuid, motherEntity);

        return new int[]{craftingInventoryId, craftingResultInventoryId, carburantInventoryId};
    }

    public void deleteFurnace(int entityId) {
        CraftingTableComponent craftingTableComponent = mCraftingTable.get(entityId);
        FurnaceComponent furnaceComponent = mFurnace.get(entityId);

        int craftingInventoryId = craftingTableComponent.craftingInventory;
        int craftingResultInventoryId = craftingTableComponent.craftingResultInventory;
        int inventoryCarburantId = furnaceComponent.inventoryCarburant;

        removeInventoryUi(craftingInventoryId);
        removeInventoryUi(craftingResultInventoryId);
        removeInventoryUi(inventoryCarburantId);
        systemsAdminCommun.getUuidEntityManager().deleteRegisteredEntity(entityId);
    }

    public EntityEdit createInventoryUi(int inventoryId, UUID inventoryUuid, int[][] inventory, int numberOfSlotParRow, int numberOfRow) {
        EntityEdit inventoryEdit = world.edit(inventoryId);
        systemsAdminCommun.getUuidEntityManager().registerEntityIdWithUuid(inventoryUuid, inventoryId);
        inventoryEdit.create(InventoryComponent.class).set(inventory, numberOfSlotParRow, numberOfRow);
        systemsAdminCommun.onContextType(ContextType.CLIENT, () -> inventoryEdit.create(InventoryUiComponent.class).set());
        return inventoryEdit;
    }

    public EntityEdit createInventoryUiIcon(int inventoryId, UUID inventoryUuid, int[][] inventory, int numberOfSlotParRow, int numberOfRow) {
        EntityEdit inventoryEdit = world.edit(inventoryId);
        systemsAdminCommun.getUuidEntityManager().registerEntityIdWithUuid(inventoryUuid, inventoryId);
        inventoryEdit.create(InventoryComponent.class).set(inventory, numberOfSlotParRow, numberOfRow);
        systemsAdminCommun.onContextType(ContextType.CLIENT, () -> inventoryEdit.create(InventoryUiComponent.class).setIcon());
        return inventoryEdit;
    }

    private EntityEdit createInventoryUi(int inventoryId, UUID inventoryUuid, int numberOfSlotParRow, int numberOfRow) {
        return createInventoryUi(inventoryId, inventoryUuid, new int[numberOfSlotParRow * numberOfRow][InventoryComponent.DEFAULT_STACK_LIMITE], numberOfSlotParRow, numberOfRow);
    }

    public InventoryComponent getChestInventory(int motherEntity) {
        return mInventory.get(getChestInventoryId(motherEntity));
    }

    public InventoryComponent getCursorInventory(int motherEntity) {
        return mInventory.get(mCursor.get(motherEntity).getInventoryId());
    }

    public int getCursorInventoryId(int motherEntity) {
        return mCursor.get(motherEntity).getInventoryId();
    }

    public int getChestInventoryId(int motherEntity) {
        return mChest.get(motherEntity).getInventoryId();
    }

    /**
     * Get the craft inventory of the entity. Where the crafting happen. Must have {@link CraftingTableComponent}.
     * @param motherEntity The mother entity must have crafting {@link CraftingTableComponent}.
     * @return The inventory craft component of the crafting table.
     */
    public InventoryComponent getCraftingInventory(int motherEntity) {
        return getCraftingInventory(mCraftingTable.get(motherEntity));
    }

    public InventoryComponent getCraftingInventory(CraftingTableComponent craftingTableComponent) {
        return mInventory.get(craftingTableComponent.craftingInventory);
    }

    /**
     * Get the result inventory of the entity. Where the crafting result is. Must have {@link CraftingTableComponent}.
     * @param motherEntity The mother entity must have crafting {@link CraftingTableComponent}.
     * @return The inventory result component of the crafting table.
     */
    public InventoryComponent getCraftingResultInventory(int motherEntity) {
        return getCraftingResultInventory(mCraftingTable.get(motherEntity));
    }

    public InventoryComponent getCraftingResultInventory(CraftingTableComponent craftingTableComponent) {
        return mInventory.get(craftingTableComponent.craftingResultInventory);
    }

    public List<List<ItemEntry>> mapInventoryToItemRegistry(int inventoryId) {
        InventoryComponent inventoryComponent = mInventory.get(inventoryId);
        List<List<ItemEntry>> items = new ArrayList<>(inventoryComponent.numberOfRow);
        for (int i = 0; i < inventoryComponent.numberOfRow; i++) {
            items.add(i, new ArrayList<>(inventoryComponent.numberOfSlotParRow));
            for (int j = 0; j < inventoryComponent.numberOfSlotParRow; j++) {
                ItemComponent itemComponent = mItem.get(inventoryComponent.inventory[(i * inventoryComponent.numberOfRow) + j][0]);
                if (itemComponent != null) {
                    items.get(i).add(j, itemComponent.itemRegisterEntry);
                } else {
                    items.get(i).add(j, null);
                }
            }
        }
        return items;
    }
}
