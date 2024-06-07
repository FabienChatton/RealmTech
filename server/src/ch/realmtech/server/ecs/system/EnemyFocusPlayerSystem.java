package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ecs.component.PlayerDeadComponent;
import ch.realmtech.server.ecs.component.PositionComponent;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.enemy.EnemyComponent;
import ch.realmtech.server.enemy.EnemyState;
import ch.realmtech.server.mod.options.server.mob.EnemyFocusPlayerDstOptionEntry;
import ch.realmtech.server.registry.RegistryUtils;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.math.Vector2;

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

    @Override
    protected void initialize() {
        super.initialize();
        enemyFocusPlayerDstOptionEntry = RegistryUtils.findEntryOrThrow(systemsAdminServer.getRootRegistry(), EnemyFocusPlayerDstOptionEntry.class);
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
            Vector2 enemyPos = enemyPositionComponent.toVector2();
            int minPlayerId = systemsAdminServer.getEnemySpawnSystemServer().getClosesPlayer(enemyPos);

            if (minPlayerId != -1) {
                float minPlayerDst = mPos.get(minPlayerId).toVector2().dst(enemyPos);
                if (minPlayerDst <= enemyFocusPlayerDstOptionEntry.getValue()) {
                    messageManager.dispatchMessage(null, enemyComponent.getEnemyTelegraph(), EnemyState.FOCUS_PLAYER_MESSAGE, minPlayerId);
                } else {
                    messageManager.dispatchMessage(null, enemyComponent.getEnemyTelegraph(), EnemyState.SLEEP_MESSAGE);
                }
            } else {
                messageManager.dispatchMessage(null, enemyComponent.getEnemyTelegraph(), EnemyState.SLEEP_MESSAGE);
            }
        };
    }
}
