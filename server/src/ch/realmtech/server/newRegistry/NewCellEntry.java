package ch.realmtech.server.newRegistry;

import ch.realmtech.server.level.cell.BreakCellEvent;
import ch.realmtech.server.level.cell.CellBehavior;
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
            Optional<? extends NewEntry> dropItemEvaluated = RegistryUtils.findEntry(rootRegistry, cellBehavior.getDropItemRegistryName());
            if (dropItemEvaluated.isEmpty()) {
                throw new InvalideEvaluate("Can not find " + cellBehavior.getDropItemRegistryName() + " registry.");
            } else {
                NewEntry dropItem = dropItemEvaluated.get();
                if (!(dropItem instanceof NewItemEntry)) {
                    throw new InvalideEvaluate(cellBehavior.getDropItemRegistryName() + " not a instanceof NewItemEntry");
                } else {
                    cellBehavior.setBreakCellEvent(BreakCellEvent.dropOnBreak((NewItemEntry) dropItem));
                }
            }
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

    @Override
    public TextureRegion getTextureRegion(TextureAtlas textureAtlas) {
        return textureAtlas.findRegion(textureRegionName);
    }
}
