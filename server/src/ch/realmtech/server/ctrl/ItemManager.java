package ch.realmtech.server.ctrl;

import ch.realmtech.server.registry.ItemEntry;
import com.artemis.Manager;

import java.util.UUID;

public abstract class ItemManager extends Manager {
    public abstract int newItemOnGround(float worldPosX, float worldPosY, ItemEntry itemRegisterEntry, UUID itemUuid);

    public abstract int newItemInventory(ItemEntry itemRegisterEntry, UUID itemUuid);
}
