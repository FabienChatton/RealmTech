package ch.realmtech.server.ecs.component;

import com.artemis.Component;
import com.artemis.World;
import com.artemis.managers.TagManager;

import java.util.HashMap;


public class PlayerComponent extends Component {
    public HashMap<String, Long> playerSoundLoop;

    public PlayerComponent() {
        playerSoundLoop = new HashMap<>();
    }

    /**
     * Seulement sur le client
     */
    public static boolean isMainPlayer(int playerId, World world) {
        return world.getSystem(TagManager.class).getEntityId("MAIN_PLAYER") == playerId;
    }
}
