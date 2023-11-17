package ch.realmtech.server.ctrl;

import ch.realmtech.server.registery.ItemRegisterEntry;
import com.artemis.Manager;

import java.util.UUID;

public abstract class ItemManager extends Manager {
    public abstract int newItemOnGround(float worldPosX, float worldPosY, ItemRegisterEntry itemRegisterEntry, UUID itemUuid);
    public abstract int newItemInventory(ItemRegisterEntry itemRegisterEntry, UUID itemUuid);
}
