package ch.realmtech.server.registry;

import ch.realmtech.server.item.ItemBehavior;

public class IconEntry extends ItemEntry {
    public IconEntry(String name, String textureRegionName) {
        super(name, textureRegionName, ItemBehavior.builder().icon().build());
    }
}
