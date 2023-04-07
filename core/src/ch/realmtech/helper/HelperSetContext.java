package ch.realmtech.helper;

import ch.realmtech.RealmTech;


public interface HelperSetContext {
    static void setContext(RealmTech context) {
        PopupHelper.context = context;
    }
}
