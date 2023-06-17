package ch.realmtech.game.registery;

import ch.realmtech.game.level.cell.CellBehavior;
import ch.realmtech.game.mod.RealmTechCoreMod;
import com.artemis.Archetype;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class CellRegisterEntry implements RegistryEntry {
    private Archetype archetype;
    private final TextureRegion textureRegion;
    private final CellBehavior cellBehavior;

    /**
     * Trouve un registre via le hash le nom de son mod + le nom de la cellule.
     *
     * @param cellRegisterHash La hash de clé (mod + nom cellule) que l'ont souhait connaitre le registre.
     * @return Le registre qui correspond au hash sinon null
     */
    public static CellRegisterEntry getCellModAndCellHash(int cellRegisterHash) {
        CellRegisterEntry ret = null;
        for (String key : RealmTechCoreMod.REALM_TECH_CORE_CELL_REGISTRY.keySet()) {
            int keyHash = key.hashCode();
            if (keyHash == cellRegisterHash) {
                ret = RealmTechCoreMod.REALM_TECH_CORE_CELL_REGISTRY.get(key);
                break;
            }
        }
        return ret;
    }

    public TextureRegion getTextureRegion() {
        return textureRegion;
    }

    public CellBehavior getCellBehavior() {
        return cellBehavior;
    }

    public Archetype getArchetype() {
        return archetype;
    }

    public CellRegisterEntry(Archetype archetype, TextureRegion textureRegion, CellBehavior cellBehavior) {
        this.archetype = archetype;
        this.textureRegion = textureRegion;
        this.cellBehavior = cellBehavior;
    }

    public CellRegisterEntry(TextureRegion textureRegion, CellBehavior cellBehavior) {
        this.textureRegion = textureRegion;
        this.cellBehavior = cellBehavior;
    }
}
