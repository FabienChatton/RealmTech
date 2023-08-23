package ch.realmtech.game.registery;

import ch.realmtech.RealmTech;
import ch.realmtech.game.item.ItemBehavior;
import ch.realmtech.game.mod.RealmTechCoreMod;
import ch.realmtech.helper.Lazy;
import com.artemis.Archetype;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.NoSuchElementException;

public class ItemRegisterEntry implements Entry<ItemRegisterEntry> {
    private Archetype archetype;
    @Lazy(champSource = "textureRegionName")
    private TextureRegion textureRegion;
    private String textureRegionName;
    private final ItemBehavior itemBehavior;
    private Registry<ItemRegisterEntry> registry;

    public Archetype getArchetype() {
        return archetype;
    }

    public TextureRegion getTextureRegion(RealmTech context) {
        if (textureRegion == null) {
            textureRegion = context.getTextureAtlas().findRegion(textureRegionName);
            if (this.textureRegion == null) {
                throw new IllegalArgumentException("vous n'avez pas mit une texture valide. Cette texture n'est pas été trouvé dans l'atlas \"" + textureRegionName + "\"");
            }
        }
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
        this.textureRegionName = textureRegionName;
        this.itemBehavior = itemBehavior;
    }

    @Override
    public String toString() {
        return registry != null ? findRegistryEntryToString(registry) : textureRegionName;
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
                .orElseThrow(() -> new NoSuchElementException("On dirait qu'un item est présente dans la sauvegarde mais dans le jeu. La sauvegarde n'a pas pu être chargé. Hash de l'item en question \"" + itemModIdHash + "\""))
                .getEntry();
    }

    public TextureRegion getTextureRegion() {
        return textureRegion;
    }

    public String getTextureRegionName() {
        return textureRegionName;
    }

    @Override
    public void setRegistry(Registry<ItemRegisterEntry> registry) {
        this.registry = registry;
    }
}
