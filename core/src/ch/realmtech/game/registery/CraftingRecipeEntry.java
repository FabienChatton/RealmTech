package ch.realmtech.game.registery;


import ch.realmtech.game.registery.infRegistry.InfEntry;

public interface CraftingRecipeEntry extends RegistryEntry, InfEntry {
    /**
     * Donne en entrer un tableau à deux dimensions comme :
     * <pre>
     *     [][][]
     *     [][][]
     *     [][][]
     * </pre>
     * qui représente l'inventaire du craft avec comme cellule les registres des items.
     * Pour le moment la logic pour valider le craft dans la méthode à définir.
     * @param itemRegisterEntries Un tableau d'items qui va passer le test pour savoir si le craft est valide
     * @return le registre de l'item si le craft est réussie sinon null
     */
    ItemRegisterEntry craft(ItemRegisterEntry[][] itemRegisterEntries);
}
