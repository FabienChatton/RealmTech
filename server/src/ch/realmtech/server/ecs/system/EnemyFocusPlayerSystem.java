package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ecs.component.PlayerConnexionComponent;
import ch.realmtech.server.ecs.component.PlayerDeadComponent;
import ch.realmtech.server.ecs.component.PositionComponent;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.enemy.EnemyComponent;
import ch.realmtech.server.enemy.EnemyState;
import ch.realmtech.server.mod.options.server.mob.EnemyDispawnDstOptionEntry;
import ch.realmtech.server.mod.options.server.mob.EnemyFocusPlayerDstOptionEntry;
import ch.realmtech.server.registry.RegistryUtils;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.ai.msg.MessageManager;

import java.util.function.IntConsumer;

@All({EnemyComponent.class, PositionComponent.class})
public class EnemyFocusPlayerSystem extends IteratingSystem {
    private final MessageManager messageManager = MessageManager.getInstance();
    @Wire
    private SystemsAdminServer systemsAdminServer;
    private ComponentMapper<PositionComponent> mPos;
    private ComponentMapper<EnemyComponent> mEnemy;
    private ComponentMapper<PlayerDeadComponent> mDead;
    private EnemyFocusPlayerDstOptionEntry enemyFocusPlayerDstOptionEntry;
    private EnemyDispawnDstOptionEntry enemyDispawnDstOptionEntry;

    @Override
    protected void initialize() {
        super.initialize();
        enemyFocusPlayerDstOptionEntry = RegistryUtils.findEntryOrThrow(systemsAdminServer.getRootRegistry(), EnemyFocusPlayerDstOptionEntry.class);
        enemyDispawnDstOptionEntry = RegistryUtils.findEntryOrThrow(systemsAdminServer.getRootRegistry(), EnemyDispawnDstOptionEntry.class);
    }

    @Override
    protected void process(int entityId) {
        EnemyComponent enemyComponent = mEnemy.get(entityId);
        enemyComponent.getUpdateEnemy().accept(entityId);
    }

    public IntConsumer enemyFocusPlayer() {
        return (entityId) -> {
            EnemyComponent enemyComponent = mEnemy.get(entityId);
            PositionComponent enemyPositionComponent = mPos.get(entityId);
            IntBag players = world.getAspectSubscriptionManager().get(Aspect.all(
                    PlayerConnexionComponent.class
            ).exclude(
                    PlayerDeadComponent.class
            )).getEntities();

            int[] playerData = players.getData();
            float minPlayerDst = Float.MAX_VALUE;
            int minPlayerId = -1;
            for (int i = 0; i < players.size(); i++) {
                int playerId = playerData[i];
                PositionComponent playerPositionComponent = mPos.get(playerId);
                float dst = enemyPositionComponent.toVector2().dst(playerPositionComponent.toVector2());

                if (dst < minPlayerDst) {
                    minPlayerDst = dst;
                    minPlayerId = playerId;
                }
            }

            if (minPlayerId != -1) {
                if (minPlayerDst <= enemyFocusPlayerDstOptionEntry.getValue()) {
                    messageManager.dispatchMessage(null, enemyComponent.getEnemyTelegraph(), EnemyState.FOCUS_PLAYER_MESSAGE, minPlayerId);
                } else if (minPlayerDst >= enemyDispawnDstOptionEntry.getValue()) {
                    systemsAdminServer.getIaTestSystem().destroyEnemyServer(entityId);
                } else {
                    messageManager.dispatchMessage(null, enemyComponent.getEnemyTelegraph(), EnemyState.SLEEP_MESSAGE);
                }
            } else {
                messageManager.dispatchMessage(null, enemyComponent.getEnemyTelegraph(), EnemyState.SLEEP_MESSAGE);
            }
        };
    }
}
