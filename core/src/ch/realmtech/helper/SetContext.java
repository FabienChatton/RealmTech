package ch.realmtech.helper;

import ch.realmtech.RealmTech;
import ch.realmtech.game.level.cell.CellBehavior;
import ch.realmtech.game.registery.CellRegisterEntry;
import ch.realmtech.game.registery.ItemRegisterEntry;


public interface SetContext {
    static void setContext(RealmTech context) {
        Popup.context = context;
        CellRegisterEntry.context = context;
        CellBehavior.context = context;
        ItemRegisterEntry.context = context;
    }
}
