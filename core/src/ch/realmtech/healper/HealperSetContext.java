package ch.realmtech.healper;

import ch.realmtech.RealmTech;


public interface HealperSetContext {
    static void setContext(RealmTech context) {
        PopupHealper.context = context;
    }
}
