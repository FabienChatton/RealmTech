package ch.realmtech.core.game.ecs.component;

import com.artemis.Component;
import com.badlogic.gdx.graphics.Color;

public class TextureColorComponent extends Component {
    private Color color;

    public TextureColorComponent set(Color color) {
        this.color = color;
        return this;
    }

    public Color getColor() {
        return color;
    }
}
