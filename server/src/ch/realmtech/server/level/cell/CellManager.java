package ch.realmtech.server.level.cell;

import ch.realmtech.server.registery.ItemRegisterEntry;
import com.badlogic.gdx.utils.Null;

public interface CellManager {

    void breakCell(int worldPosX, int worldPosY, @Null ItemRegisterEntry itemDropRegisterEntry);
}
