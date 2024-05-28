package ch.realmtech.server.registry;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class MobEntry extends Entry {
    private final String textureRegionName;

    public MobEntry(String name, String textureRegionName) {
        super(name);
        this.textureRegionName = textureRegionName;
    }

    public TextureRegion getTextureRegion(TextureAtlas textureAtlas) {
        return textureAtlas.findRegion(textureRegionName);
    }
}
