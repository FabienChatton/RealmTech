package ch.realmtech.server.newRegistry;

import ch.realmtech.server.item.ItemBehavior;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Optional;

public abstract class NewItemEntry extends NewEntry {
    private final ItemBehavior itemBehavior;
    private final String textureRegionName;

    public NewItemEntry(String name, String textureRegionName, ItemBehavior itemBehavior) {
        super(name);
        this.textureRegionName = textureRegionName;
        this.itemBehavior = itemBehavior;
    }

    public ItemBehavior getItemBehavior() {
        return itemBehavior;
    }

    @Override
    public void evaluate(NewRegistry<?> rootRegistry) throws InvalideEvaluate {
        if (itemBehavior.getPlaceCellName() != null) {
            Optional<? extends NewEntry> placeCellEvaluate = RegistryUtils.findEntry(rootRegistry, itemBehavior.getPlaceCellName());
            if (placeCellEvaluate.isEmpty()) {
                throw new InvalideEvaluate("Can not find " + itemBehavior.getPlaceCellName() + " registry.");
            } else {
                NewEntry placeCell = placeCellEvaluate.get();
                if ((!(placeCell instanceof NewCellEntry))) {
                    throw new InvalideEvaluate(itemBehavior.getPlaceCellName() + " not a instanceof NewCellEntry");
                } else {
                    itemBehavior.setPlaceCell(((NewCellEntry) placeCell));
                }
            }
        }
    }

    @Override
    public TextureRegion getTextureRegion(TextureAtlas textureAtlas) {
        return textureAtlas.findRegion(textureRegionName);
    }
}
