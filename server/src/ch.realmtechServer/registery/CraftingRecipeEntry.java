package ch.realmtechServer.registery;


import ch.realmtechServer.craft.CraftResult;

import java.util.Optional;

public interface CraftingRecipeEntry extends Entry<CraftingRecipeEntry> {
    /**
     * Permet de connaitre de craft avec des items en entre et un item en sortie.
     *
     * @param itemRegisterEntry L'inventaire du craft les items dans leur forme de registre.
     * @param width
     * @param height
     * @return Le registre de l'item résultat du craft s'il est valide.
     */
    Optional<CraftResult> craft(final ItemRegisterEntry[] itemRegisterEntry, int width, int height);
}
