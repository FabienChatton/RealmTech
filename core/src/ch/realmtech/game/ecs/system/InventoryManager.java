package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.component.InventoryComponent;
import ch.realmtech.game.ecs.component.ItemComponent;
import ch.realmtech.game.ecs.component.TextureComponent;
import com.artemis.ComponentMapper;
import com.artemis.Manager;
import com.artemis.annotations.Wire;

import java.util.Arrays;

public class InventoryManager extends Manager {
    @Wire(name = "context")
    private RealmTech context;

    private ComponentMapper<InventoryComponent> mInventory;
    private ComponentMapper<TextureComponent> mTexture;
    private ComponentMapper<ItemComponent> mItem;

    /**
     * Parcourt l'inventaire à la recherche d'un emplacement disponible pour ajouter l'item
     * @param itemId l'item souhaité a ajouter.
     * @param entityId L'entité où l'item sera ajouté.
     * @return vrai si l'item a été ajouté avec success.
     */
    public boolean addItemToInventory(int itemId, int entityId) {
        final InventoryComponent inventoryComponent = mInventory.get(entityId);
        for (int i = 0; i < inventoryComponent.inventory.length; i++) {
            if (addItemToStack(inventoryComponent.inventory[i], itemId)) {
                return true;
            }
        }
        return false;
    }

    public boolean moveStackToStackNumber(int[] stackSrc, int[] stackDst, int nombre) {
        for (int i = 0, j = tailleStack(stackSrc) - 1; i < nombre && j >= 0; i++, j--) {
            if (!addItemToStack(stackDst, stackSrc[j])) {
                return false;
            }
            stackSrc[j] = 0;
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
     * Déplace une stack au maximum vers une autre stack
     * @param stackSrc la stack à déplacer.
     * @param stackDst la stack destination.
     * @return true si quelque chose a été déplacé
     */
    public boolean moveStackToStack(int[] stackSrc, int[] stackDst) {
        if (stackSrc[0] == 0) return false;
        if (stackDst[0] == 0) {
            System.arraycopy(stackSrc, 0, stackDst, 0, tailleStack(stackSrc)); // déplace toute la stack si la stack de destination est vide
            // vide la stack source
            final int tailleStack = tailleStack(stackSrc);
            for (int i = 0; i < tailleStack; i++) {
                stackSrc[i] = 0;
            }
            return true;
        }
        final ItemComponent itemComponentSrc = mItem.get(stackSrc[0]);
        final ItemComponent itemComponentDst = mItem.get(stackDst[0]);

        if (!(itemComponentSrc.itemRegisterEntry == itemComponentDst.itemRegisterEntry)) {
            return false;
        }
        if (stackDst.length < tailleStack(stackDst)) {
            return false; // on ne peut pas ajouter des items si la stack est déjà remplie
        }

        boolean deplace = false;
        // déplace de la stack source à la stack destination en vidant la stack source
        for (int i = tailleStack(stackSrc) - 1, j = tailleStack(stackDst); i >= 0 && j < stackDst.length; i--, j++) {
            stackDst[j] = stackSrc[i];
            stackSrc[i] = 0;
            deplace = true;
        }
        return deplace;
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
    public void dropItemFromPlayerInventory(int itemId, int playerId, float worldPositionX, float worldPositionY) {
//        InventoryComponent inventoryComponent = mInventory.create(playerId);
//        inventoryComponent.inventory.removeValue(itemId);
//        world.getSystem(ItemManager.class).setItemTexturePositionAndPhysicBody(
//                itemId,
//                mTexture.create(itemId).texture,
//                worldPositionX,
//                worldPositionY
//        );
//        world.getSystem(SoundManager.class).playItemDrop();
    }

    public int[][] getInventory(int entityId) {
        return mInventory.get(entityId).inventory;
    }

    public static void clearInventory(int[][] inventory) {
        for (int[] stack : inventory) {
            clearStack(stack);
        }
    }

    public static void clearStack(int[] stack) {
        Arrays.fill(stack, 0);
    }

    public void removeInventory(int[][] inventory) {
        for (int[] stack : inventory) {
            removeStack(stack);
        }
    }

    private void removeStack(int[] stack) {
        final int tailleStack = InventoryManager.tailleStack(stack);
        for (int i = 0; i < tailleStack; i++) {
            world.delete(stack[i]);
        }
        clearStack(stack);
    }

    public void removeOneItem(int[] stack) {
        int taille = tailleStack(stack);
        if (taille == 0) return;
        world.delete(stack[taille - 1]);
        stack[taille - 1] = 0;
    }

    public void removeAllOneItem(int[][] inventory) {
        for (int i = 0; i < inventory.length; i++) {
            removeOneItem(inventory[i]);
        }
    }
}
