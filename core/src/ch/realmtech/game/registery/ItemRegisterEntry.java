package ch.realmtech.game.registery;

import ch.realmtech.game.item.ItemBehavior;
import com.artemis.Archetype;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ItemRegisterEntry implements RegistryEntry {
    private Archetype archetype;
    private final TextureRegion textureRegion;
    private final ItemBehavior itemBehavior;

    public Archetype getArchetype() {
        return archetype;
    }

    public TextureRegion getTextureRegion() {
        return textureRegion;
    }

    public ItemBehavior getItemBehavior() {
        return itemBehavior;
    }

    public ItemRegisterEntry(Archetype archetype, TextureRegion textureRegion, ItemBehavior itemBehavior) {
        this.archetype = archetype;
        this.textureRegion = textureRegion;
        this.itemBehavior = itemBehavior;
    }

    public ItemRegisterEntry(TextureRegion textureRegion, ItemBehavior itemBehavior) {
        this.textureRegion = textureRegion;
        this.itemBehavior = itemBehavior;
    }

    @Override
    public String toString() {
        return  "Archetype : " + archetype + "\n" +
                "\tTextureRegion : " + textureRegion + "\n" +
                "\tItemBehavior : " + itemBehavior;
    }
}
