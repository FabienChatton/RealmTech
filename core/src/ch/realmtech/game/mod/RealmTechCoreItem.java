package ch.realmtech.game.mod;


import ch.realmtech.game.item.ItemBehavior;
import ch.realmtech.game.item.ItemType;
import ch.realmtech.game.registery.ItemRegisterEntry;
import ch.realmtech.game.registery.Registry;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
public class RealmTechCoreItem {

    public final static String PIOCHE_ITEM = "item.pioche";
    public final static String PELLE_ITEM = "item.pelle";

    static void initItem(Registry<ItemRegisterEntry> registry, TextureAtlas textureAtlas) {
        registry.put(PIOCHE_ITEM, new ItemRegisterEntry(
                textureAtlas.findRegion("pioche-01"),
                new ItemBehavior.Builder()
                        .setItemType(ItemType.PIOCHE)
                        .build()
                ));

        registry.put(PELLE_ITEM, new ItemRegisterEntry(
                textureAtlas.findRegion("pelle-01"),
                new ItemBehavior.Builder()
                        .setItemType(ItemType.PELLE)
                        .build()
        ));
    }
}
