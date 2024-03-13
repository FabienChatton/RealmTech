package ch.realmtech.server.newRegistry;

import ch.realmtech.server.item.ItemBehavior;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

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
        super.evaluate(rootRegistry);
        if (itemBehavior.getPlaceCellName() != null) {
            itemBehavior.setPlaceCell(RegistryUtils.evaluateSafe(rootRegistry, itemBehavior.getPlaceCellName(), NewCellEntry.class));
        }
    }

    public TextureRegion getTextureRegion(TextureAtlas textureAtlas) {
        return textureAtlas.findRegion(textureRegionName);
    }
}
