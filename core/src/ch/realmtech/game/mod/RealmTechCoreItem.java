package ch.realmtech.game.mod;


import ch.realmtech.game.item.ItemBehavior;
import ch.realmtech.game.item.ItemType;
import ch.realmtech.game.registery.ItemRegisterEntry;
import ch.realmtech.game.registery.Registry;

public class RealmTechCoreItem {

    public final static ItemRegisterEntry PIOCHE_ITEM = new ItemRegisterEntry(
            "pioche-01",
            new ItemBehavior.Builder()
                    .setItemType(ItemType.PIOCHE)
                    .build()
    );
    public final static ItemRegisterEntry PELLE_ITEM = new ItemRegisterEntry(
            "pelle-01",
            new ItemBehavior.Builder()
                    .setItemType(ItemType.PELLE)
                    .build()
    );
    public final static ItemRegisterEntry SANDALES_ITEM = new ItemRegisterEntry(
            "sandales-01",
            new ItemBehavior.Builder()
                    .setSpeedEffect(2)
                    .build()
    );

    public static void initItem(Registry<ItemRegisterEntry> registry) {
        registry.add("pioche", PIOCHE_ITEM);
        registry.add("pelle", PELLE_ITEM);
        registry.add("sandales", SANDALES_ITEM);
    }
}
