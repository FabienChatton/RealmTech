package ch.realmtech.game.registery;


public interface CraftingRecipeEntry extends RegistryEntry {
    ItemRegisterEntry craft(ItemRegisterEntry[][] itemRegisterEntries);
}
