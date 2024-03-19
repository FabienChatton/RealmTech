package ch.realmtech.server.newRegistry;

import ch.realmtech.server.item.ItemBehavior;

public class IconEntry extends NewItemEntry {
    public IconEntry(String name, String textureRegionName) {
        super(name, textureRegionName, ItemBehavior.builder().icon().build());
    }
}
