package ch.realmtech.game.mod;

import ch.realmtech.game.registery.CraftingRecipeEntry;
import ch.realmtech.game.registery.RegistryAnonyme;

public class RealmTechCoreCraftingRecipe {
    public static void initCraftingRecipe(RegistryAnonyme<CraftingRecipeEntry> registry) {
        registry.add(itemRegisterEntries -> {
            if (    itemRegisterEntries[0][0] == RealmTechCoreItem.PELLE_ITEM &&
                    itemRegisterEntries[0][1] == RealmTechCoreItem.PELLE_ITEM) {
                return RealmTechCoreItem.PIOCHE_ITEM;
            } else {
                return null;
            }
        });
        registry.add(itemRegisterEntries -> {
            if (    itemRegisterEntries[0][2] == RealmTechCoreItem.PELLE_ITEM &&
                    itemRegisterEntries[2][2] == RealmTechCoreItem.PELLE_ITEM) {
                return RealmTechCoreItem.SANDALES_ITEM;
            } else {
                return null;
            }
        });
    }
}
