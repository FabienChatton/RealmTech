package ch.realmtech.server.level.cell;

import ch.realmtech.server.newRegistry.NewItemEntry;

public interface CellManager {

    void breakCell(int worldPosX, int worldPosY, NewItemEntry itemDropRegisterEntry, int playerSrc);
}
