package ch.realmtech.server.ecs.component;

import ch.realmtech.server.divers.FixList;
import com.artemis.Component;
import com.artemis.World;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import java.util.List;


public class PlayerComponent extends Component {
    public boolean moveUp;
    public boolean moveDown;
    public boolean moveLeft;
    public boolean moveRight;
    public TextureRegion[] animationFront;
    public TextureRegion[] animationLeft;
    public TextureRegion[] animationBack;
    public TextureRegion[] animationRight;
    public float laps = 0.3f;
    public float cooldown = laps;
    public int animationIndex = 0;
    public byte lastDirection = 0;
    public List<Vector2> oldPoss;

    public PlayerComponent() {
        oldPoss = new FixList<>(10);
    }

    /**
     * Seulement sur le client
     */
    public static boolean isMainPlayer(int playerId, World world) {
        return world.getSystem(TagManager.class).getEntityId("MAIN_PLAYER") == playerId;
    }
}
