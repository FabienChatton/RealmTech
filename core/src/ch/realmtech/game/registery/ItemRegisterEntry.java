package ch.realmtech.game.registery;

import ch.realmtech.RealmTech;
import ch.realmtech.game.item.ItemBehavior;
import ch.realmtech.helper.SetContext;
import com.artemis.Archetype;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ItemRegisterEntry implements Entry, SetContext {
    private Archetype archetype;
    private final TextureRegion textureRegion;
    private final ItemBehavior itemBehavior;
    public static RealmTech context;

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

    public ItemRegisterEntry(String textureRegionName, ItemBehavior itemBehavior) {
        this.textureRegion = context.getTextureAtlas().findRegion(textureRegionName);
        this.itemBehavior = itemBehavior;
    }

    @Override
    public String toString() {
        return textureRegion.toString();
    }
}
