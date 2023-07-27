package ch.realmtech.game.registery;

import ch.realmtech.RealmTech;
import ch.realmtech.game.item.ItemBehavior;
import ch.realmtech.game.mod.RealmTechCoreMod;
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
        if (this.textureRegion == null) throw new IllegalArgumentException("vous n'avez pas mit une texture valide");
        this.itemBehavior = itemBehavior;
    }

    @Override
    public String toString() {
        return textureRegion.toString();
    }

    public static int getHash(ItemRegisterEntry itemRegisterEntry) {
        return RealmTechCoreMod.ITEMS.getEnfants().stream()
                .filter(itemRegisterEntryRegistryEntry -> itemRegisterEntryRegistryEntry.getEntry() == itemRegisterEntry)
                .findFirst()
                .orElseThrow()
                .getID()
                .hashCode();
    }

    public int getHash() {
        return getHash(this);
    }


    public static ItemRegisterEntry getItemByHash(int itemModIdHash) {
        return RealmTechCoreMod.ITEMS.getEnfants().stream()
                .filter(itemRegisterEntryRegistryEntry -> itemRegisterEntryRegistryEntry.getID().hashCode() == itemModIdHash)
                .findFirst()
                .orElseThrow()
                .getEntry();
    }
}
