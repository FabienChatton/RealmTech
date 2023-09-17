package ch.realmtechCommuns.ecs.component;

import com.artemis.Component;
import com.artemis.World;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


public class PlayerComponent extends Component {
    public final static String TAG = "PLAYER";
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
    /**
     * 0 front, 1 left, 2 back, 3 right
     */
    public byte lastDirection = 0;

    /**
     * Seulement sur le client
     */
    public static boolean isMainPlayer(int playerId, World world) {
        return world.getSystem(TagManager.class).getEntityId("MAIN_PLAYER") == playerId;
    }

    public static int getMainPlayer(World world) {
        return world.getSystem(TagManager.class).getEntityId("MAIN_PLAYER");
    }
}
