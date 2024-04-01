package ch.realmtech.core.game.ecs.component;

import com.artemis.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TextureImportantComponent extends Component {
    private TextureRegion[] textureRegions;
    private int tickElapse;
    private int tickEnd;
    private int indexArray;
    private int index;

    public TextureImportantComponent set(TextureRegion[] textureRegions, int tickElapse, int tickEnd) {
        this.textureRegions = textureRegions;
        this.tickElapse = tickElapse;
        this.tickEnd = tickEnd;
        return this;
    }

    public TextureRegion getTexture() {
        if (++index >= tickElapse) {
            indexArray++;
        }
        if (index >= tickEnd) {
            return null;
        }
        if (indexArray < textureRegions.length) {
            indexArray = 0;
        }
        return textureRegions[indexArray];
    }
}
