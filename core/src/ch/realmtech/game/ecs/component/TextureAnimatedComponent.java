package ch.realmtech.game.ecs.component;

import com.artemis.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TextureAnimatedComponent extends Component {
    public TextureRegion[] animation;
    public float laps = 0;
    public float cooldown = 0;
    public int index = 0;

    public void set(TextureRegion[] animation, float laps) {
        this.animation = animation;
        this.laps = laps;
        this.cooldown = laps;
    }
}
