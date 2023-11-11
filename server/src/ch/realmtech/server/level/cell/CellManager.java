package ch.realmtech.server.level.cell;

import ch.realmtech.server.registery.ItemRegisterEntry;

public interface CellManager {

    void breakCell(int worldPosX, int worldPosY, ItemRegisterEntry itemDropRegisterEntry);
}
