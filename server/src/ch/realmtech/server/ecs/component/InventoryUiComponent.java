package ch.realmtech.server.ecs.component;

import com.artemis.Component;

public class InventoryUiComponent extends Component {
    public final static String DEFAULT_BACKGROUND_TEXTURE_NAME = "inventory-02";
    public final static String NO_TEXTURE_BACKGROUND_TEXTURE_NAME = "no-texture";
    public String backgroundTexture;

    public InventoryUiComponent set() {
        this.backgroundTexture = DEFAULT_BACKGROUND_TEXTURE_NAME;
        return this;
    }

    public InventoryUiComponent setIcon() {
        this.backgroundTexture = NO_TEXTURE_BACKGROUND_TEXTURE_NAME;
        return this;
    }

    public InventoryUiComponent set(String backgroundTexture) {
        this.backgroundTexture = backgroundTexture;
        return this;
    }
}
