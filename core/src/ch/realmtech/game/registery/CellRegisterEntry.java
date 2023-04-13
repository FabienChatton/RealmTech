package ch.realmtech.game.registery;

import ch.realmtech.game.level.cell.CellBehavior;
import com.artemis.Archetype;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class CellRegisterEntry implements RegistryEntry {
    private Archetype archetype;
    private final TextureRegion textureRegion;
    private final CellBehavior cellBehavior;

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
