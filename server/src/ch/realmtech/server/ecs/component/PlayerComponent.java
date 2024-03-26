package ch.realmtech.server.ecs.component;

import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import com.artemis.Component;
import com.artemis.World;

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
        return ((SystemsAdminCommun) world.getRegistered("systemsAdmin")).getTagManager().getEntityId("MAIN_PLAYER") == playerId;
    }
}
