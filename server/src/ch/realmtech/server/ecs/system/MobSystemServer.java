package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ecs.component.Box2dComponent;
import ch.realmtech.server.ecs.component.PositionComponent;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.ia.IaComponent;
import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.ai.msg.MessageManager;

public class MobSystemServer extends BaseSystem {
    @Wire
    private SystemsAdminServer systemsAdminServer;
    private ComponentMapper<PositionComponent> mPos;
    private ComponentMapper<Box2dComponent> mBox2d;
    private final MessageManager messageManager = MessageManager.getInstance();

    @Override
    protected void initialize() {
        super.initialize();
        messageManager.setDebugEnabled(false);
    }

    @Override
    protected void processSystem() {
        naturalSpawnIa();
        messageManager.update();
    }

    private void naturalSpawnIa() {
        IntBag iaEntities = world.getAspectSubscriptionManager().get(Aspect.all(IaComponent.class)).getEntities();
        IntBag players = systemsAdminServer.getPlayerManagerServer().getPlayers();

        if (iaEntities.isEmpty()) {
            for (int i = 0; i < players.size(); i++) {
                int playerId = players.get(i);
                PositionComponent playerPosition = mPos.get(playerId);

                systemsAdminServer.getMobManager().createMobTest(playerPosition.x + 5, playerPosition.y, playerId);
            }
        }
    }
}
