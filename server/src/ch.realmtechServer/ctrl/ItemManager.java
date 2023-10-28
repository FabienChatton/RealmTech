package ch.realmtechServer.ctrl;

import ch.realmtechServer.registery.ItemRegisterEntry;
import com.artemis.Manager;

public abstract class ItemManager extends Manager {
    public abstract int newItemOnGround(float worldPosX, float worldPosY, ItemRegisterEntry itemRegisterEntry);
    public abstract int newItemInventory(ItemRegisterEntry itemRegisterEntry);
}
