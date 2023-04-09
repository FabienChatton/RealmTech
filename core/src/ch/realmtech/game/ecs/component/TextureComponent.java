package ch.realmtech.game.ecs.component;

import com.artemis.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TextureComponent extends Component {
    public TextureRegion texture;

    public void init(TextureRegion texture) {
        this.texture = texture;
    }

}
