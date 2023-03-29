package ch.realmtech.ecs.component;

import ch.realmtech.ecs.PoolableComponent;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class TextureComponent implements PoolableComponent {
    public Texture texture;

    public TextureComponent(Texture texture) {
        this.texture = texture;
    }

    @Override
    public void reset() {
        texture = null;
    }
}
