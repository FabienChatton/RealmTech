package ch.realmtech.game.registery;


import ch.realmtech.game.craft.CraftResult;

import java.util.List;
import java.util.Optional;

public interface CraftingRecipeEntry extends Entry {
    /**
     * Permet de connaitre de craft avec des items en entre et un item en sortie.
     * @param itemRegisterEntry L'inventaire du craft les items dans leur forme de registre.
     * @return Le registre de l'item résultat du craft s'il est valide.
     */
    Optional<CraftResult> craft(final ItemRegisterEntry[] itemRegisterEntry);
    List<ItemRegisterEntry> getRequireItem();
}
