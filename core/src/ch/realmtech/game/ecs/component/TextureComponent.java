package ch.realmtech.game.ecs.component;

import ch.realmtech.game.ecs.PoolableComponent;
import com.badlogic.gdx.graphics.Texture;

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
