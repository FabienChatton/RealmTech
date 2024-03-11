package ch.realmtech.server.registery;


import ch.realmtech.server.craft.CraftResult;
import ch.realmtech.server.newRegistry.NewItemEntry;

import java.util.List;
import java.util.Optional;

public interface CraftingRecipeEntry extends Entry<CraftingRecipeEntry> {
    /**
     * Permet de connaitre de craft avec des items en entre et un item en sortie.
     *
     * @param itemRegisterEntry L'inventaire du craft les items dans leur forme de registre.
     * @return Le registre de l'item résultat du craft s'il est valide.
     */
    Optional<CraftResult> craft(final List<NewItemEntry> itemRegisterEntry);
}
