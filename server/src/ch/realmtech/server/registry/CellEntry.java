package ch.realmtech.server.registry;

import ch.realmtech.server.level.cell.BreakCellEvent;
import ch.realmtech.server.level.cell.CellBehavior;
import ch.realmtech.server.level.cell.EditEntity;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Optional;

public abstract class CellEntry extends Entry {
    private final CellBehavior cellBehavior;
    private final String textureRegionName;
    private TextureRegion textureRegionCache;

    public CellEntry(String name, String textureRegionName, CellBehavior newcellBehavior) {
        super(name);
        this.textureRegionName = textureRegionName;
        this.cellBehavior = newcellBehavior;
    }

    @Override
    public void evaluate(Registry<?> rootRegistry) throws InvalideEvaluate {
        if (cellBehavior.getDropItemRegistryName() != null) {
            cellBehavior.setBreakCellEvent(BreakCellEvent.dropOnBreak(RegistryUtils.evaluateSafe(rootRegistry, cellBehavior.getDropItemRegistryName(), ItemEntry.class)));
        } else {
            cellBehavior.setBreakCellEvent(BreakCellEvent.dropNothing());
        }
    }

    public CellBehavior getCellBehavior() {
        return cellBehavior;
    }

    public String getTextureRegionName() {
        return textureRegionName;
    }

    public TextureRegion getTextureRegion(TextureAtlas textureAtlas) {
        if (textureRegionCache == null) {
            TextureRegion textureRegion = textureAtlas.findRegion(textureRegionName);
            if (textureRegion == null) {
                textureRegion = textureAtlas.findRegion("default-texture");
            }
            textureRegionCache = textureRegion;

        }
        return textureRegionCache;
    }

    public Optional<EditEntity> getEditEntity() {
        return Optional.empty();
    }
}
