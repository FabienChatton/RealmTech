package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.RealmTech;
import ch.realmtech.core.game.ecs.plugin.SystemsAdminClient;
import ch.realmtech.server.ecs.component.PlayerComponent;
import ch.realmtech.server.packet.serverPacket.ConsoleCommandeRequestPacket;
import ch.realmtech.server.registry.ItemEntry;
import com.artemis.ComponentMapper;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleCommandWrapperManager extends Manager {
    private final static Logger logger = LoggerFactory.getLogger(ConsoleCommandWrapperManager.class);

    @Wire(name = "context")
    private RealmTech context;
    @Wire
    private SystemsAdminClient systemsAdminClient;

    private ComponentMapper<PlayerComponent> mPlayer;

    public void giveItemToMe(ItemEntry itemEntryAsked, int numberOfItem) {
        String username = context.getAuthControllerClient().getUsername();
        if (username == null) {
            logger.debug("Fail to get username", new RuntimeException());
            return;
        }

        giveItem(itemEntryAsked, username, numberOfItem);
    }

    public void giveItem(ItemEntry itemEntryAsked, String usernameWho, int numberOfItem) {
        context.sendRequest(new ConsoleCommandeRequestPacket(String.format("give %s %s %d", usernameWho, itemEntryAsked, numberOfItem)));
    }
}
