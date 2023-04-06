package ch.realmtech.game.ecs.component;

import ch.realmtech.game.ecs.PoolableComponent;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TextureComponent implements PoolableComponent {
    public TextureRegion texture;

    public void init(TextureRegion texture) {
        this.texture = texture;
    }

    @Override
    public void reset() {
        texture = null;
    }
}
