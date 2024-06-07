package ch.realmtech.server.registry;

import ch.realmtech.server.item.ItemBehavior;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class ItemEntry extends Entry {
    private final ItemBehavior itemBehavior;
    private final String textureRegionName;

    public ItemEntry(String name, String textureRegionName, ItemBehavior itemBehavior) {
        super(name);
        this.textureRegionName = textureRegionName;
        this.itemBehavior = itemBehavior;
    }

    public ItemBehavior getItemBehavior() {
        return itemBehavior;
    }

    @Override
    public void evaluate(Registry<?> rootRegistry) throws InvalideEvaluate {
        if (itemBehavior.getPlaceCellName() != null) {
            itemBehavior.setPlaceCell(RegistryUtils.evaluateSafe(rootRegistry, itemBehavior.getPlaceCellName(), CellEntry.class));
        }
    }

    public TextureRegion getTextureRegion(TextureAtlas textureAtlas) {
        return textureAtlas.findRegion(textureRegionName);
    }

    public String getTextureRegionName() {
        return textureRegionName;
    }

}
