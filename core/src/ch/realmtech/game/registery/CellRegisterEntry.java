package ch.realmtech.game.registery;

import ch.realmtech.game.level.cell.CellBehavior;
import com.artemis.Archetype;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class CellRegisterEntry implements RegistryEntry {
    public Archetype archetype;
    public TextureRegion textureRegion;
    public CellBehavior cellBehavior;

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
