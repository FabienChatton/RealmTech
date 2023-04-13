package ch.realmtech.game.mod;


import ch.realmtech.game.item.ItemBehavior;
import ch.realmtech.game.registery.ItemRegisterEntry;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import static ch.realmtech.game.mod.RealmTechCoreMod.REALM_TECH_CORE_ITEM_REGISTRY;
public class RealmTechCoreItem {

    public final static String PIOCHE_ITEM = "item.pioche";
    public final static String PELLE_ITEM = "item.pelle";

    static void initItem(TextureAtlas textureAtlas) {
        REALM_TECH_CORE_ITEM_REGISTRY.put(PIOCHE_ITEM, new ItemRegisterEntry(
                textureAtlas.findRegion("pioche-01"),
                new ItemBehavior.Builder()
                        .build()
                ));

        REALM_TECH_CORE_ITEM_REGISTRY.put(PELLE_ITEM, new ItemRegisterEntry(
                textureAtlas.findRegion("pelle-01"),
                new ItemBehavior.Builder()
                        .build()
        ));
    }
}
