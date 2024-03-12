package ch.realmtech.server.ctrl;

import ch.realmtech.server.newRegistry.NewItemEntry;
import com.artemis.Manager;

import java.util.UUID;

public abstract class ItemManager extends Manager {
    public abstract int newItemOnGround(float worldPosX, float worldPosY, NewItemEntry itemRegisterEntry, UUID itemUuid);
    public abstract int newItemInventory(NewItemEntry itemRegisterEntry, UUID itemUuid);
}
