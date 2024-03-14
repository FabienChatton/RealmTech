package ch.realmtech.server.newRegistry;

import ch.realmtech.server.level.cell.BreakCellEvent;
import ch.realmtech.server.level.cell.CellBehavior;
import ch.realmtech.server.level.cell.EditEntity;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Optional;

public abstract class NewCellEntry extends NewEntry {
    private final CellBehavior cellBehavior;
    private final String textureRegionName;

    public NewCellEntry(String name, String textureRegionName, CellBehavior newcellBehavior) {
        super(name);
        this.textureRegionName = textureRegionName;
        this.cellBehavior = newcellBehavior;
    }

    @Override
    public void evaluate(NewRegistry<?> rootRegistry) throws InvalideEvaluate {
        if (cellBehavior.getDropItemRegistryName() != null) {
            cellBehavior.setBreakCellEvent(BreakCellEvent.dropOnBreak(RegistryUtils.evaluateSafe(rootRegistry, cellBehavior.getDropItemRegistryName(), NewItemEntry.class)));
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
        return textureAtlas.findRegion(textureRegionName);
    }

    public Optional<EditEntity> getEditEntityOnCreate() {
        return Optional.empty();
    }
}
