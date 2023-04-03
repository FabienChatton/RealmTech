package ch.realmtech.helper;

import ch.realmtech.RealmTech;


public interface HealperSetContext {
    static void setContext(RealmTech context) {
        PopupHealper.context = context;
    }
}
