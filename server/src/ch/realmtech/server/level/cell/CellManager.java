package ch.realmtech.server.level.cell;

import ch.realmtech.server.registry.ItemEntry;

public interface CellManager {

    void breakCell(int worldPosX, int worldPosY, ItemEntry itemDropRegisterEntry, int playerSrc);
}
