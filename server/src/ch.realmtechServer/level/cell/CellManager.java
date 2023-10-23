package ch.realmtechServer.level.cell;

import ch.realmtechServer.registery.ItemRegisterEntry;

public interface CellManager {

    void breakCell(int worldPosX, int worldPosY, ItemRegisterEntry itemDropRegisterEntry);
}
