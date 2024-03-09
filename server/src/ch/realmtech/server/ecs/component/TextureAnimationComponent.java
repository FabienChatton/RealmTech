package ch.realmtech.server.ecs.component;

import com.artemis.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TextureAnimationComponent extends Component {
    public float laps = 0.3f;
    public float cooldown = laps;
    public int animationIndex = 0;
    public TextureRegion[] animationFront;
    public TextureRegion[] animationLeft;
    public TextureRegion[] animationBack;
    public TextureRegion[] animationRight;
}
