package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ecs.component.PositionComponent;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.ia.IaComponent;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.math.Vector2;

@All({IaComponent.class, PositionComponent.class})
public class IaIsFocusPlayerSystem extends IteratingSystem {
    private final MessageManager messageManager = MessageManager.getInstance();
    @Wire
    private SystemsAdminServer systemsAdminServer;
    private ComponentMapper<PositionComponent> mPos;
    private ComponentMapper<IaComponent> mIa;
    @Override
    protected void process(int entityId) {
        PositionComponent iaPositionComponent = mPos.get(entityId);
        IntBag players = systemsAdminServer.playerManagerServer.getPlayers();
        int[] playerData = players.getData();
        for (int i = 0; i < players.size(); i++) {
            int playerId = playerData[i];
            PositionComponent playerPositionComponent = mPos.get(playerId);
            IaComponent iaComponent = mIa.get(entityId);
            if (Vector2.dst2(iaPositionComponent.x, iaPositionComponent.y, playerPositionComponent.x, playerPositionComponent.y) < 1) {
                messageManager.dispatchMessage(null, iaComponent.getIaTestAgent(), 1, playerId, false);
            } else {
                messageManager.dispatchMessage(null, iaComponent.getIaTestAgent(), 0);
            }
        }
    }
}
