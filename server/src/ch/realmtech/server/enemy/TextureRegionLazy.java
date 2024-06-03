package ch.realmtech.server.enemy;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TextureRegionLazy {
    private final String[] textureRegionNames;
    private TextureRegion[] textureRegions;

    public TextureRegionLazy(String... textureRegionNames) {
        this.textureRegionNames = textureRegionNames;
    }

    public TextureRegion[] getTextureRegions(TextureAtlas textureAtlas) {
        if (textureRegions == null) {
            TextureRegion[] textureRegions = new TextureRegion[textureRegionNames.length];
            for (int i = 0; i < textureRegionNames.length; i++) {
                textureRegions[i] = textureAtlas.findRegion(textureRegionNames[i]);
            }
            this.textureRegions = textureRegions;
            return textureRegions;
        } else {
            return textureRegions;
        }
    }
}
