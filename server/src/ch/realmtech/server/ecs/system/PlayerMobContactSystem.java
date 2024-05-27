package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.ecs.component.*;
import ch.realmtech.server.enemy.EnemyComponent;
import ch.realmtech.server.enemy.EnemyState;
import ch.realmtech.server.packet.clientPacket.MobAttackCoolDownPacket;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Exclude;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.UUID;

@All({PlayerConnexionComponent.class, PositionComponent.class, Box2dComponent.class})
@Exclude({InvincibilityComponent.class, PlayerDeadComponent.class})
public class PlayerMobContactSystem extends IteratingSystem {
    @Wire(name = "serverContext")
    private ServerContext serverContext;
    private ComponentMapper<PositionComponent> mPos;
    private ComponentMapper<Box2dComponent> mBox2d;
    private ComponentMapper<EnemyComponent> mEnemy;
    private ComponentMapper<LifeComponent> mLife;
    @Override
    protected void process(int entityId) {
        IntBag ias = world.getAspectSubscriptionManager().get(Aspect.all(
                EnemyComponent.class,
                EnemyHitPlayerComponent.class,
                PositionComponent.class,
                Box2dComponent.class
        ).exclude(EnemyAttackCooldownComponent.class)).getEntities();
        if (ias.isEmpty()) return;

        PositionComponent playerPositionComponent = mPos.get(entityId);
        Box2dComponent playerBox2dComponent = mBox2d.get(entityId);
        LifeComponent playerLifeComponent = mLife.get(entityId);
        Rectangle.tmp.set(playerBox2dComponent.body.getPosition().x, playerBox2dComponent.body.getPosition().y, playerBox2dComponent.widthWorld, playerBox2dComponent.heightWorld);

        for (int i = 0; i < ias.size(); i++) {
            int ia = ias.get(i);
            EnemyComponent enemyComponent = mEnemy.get(ia);
            PositionComponent iaPositionComponent = mPos.get(ia);
            Box2dComponent iaBox2dComponent = mBox2d.get(ia);
            Rectangle.tmp2.set(iaBox2dComponent.body.getPosition().x, iaBox2dComponent.body.getPosition().y, iaBox2dComponent.widthWorld, iaBox2dComponent.heightWorld);

            if (Rectangle.tmp.overlaps(Rectangle.tmp2)) {
                MessageManager.getInstance().dispatchMessage(null, enemyComponent.getEnemyTelegraph(), EnemyState.ATTACK_COOLDOWN_MESSAGE);
                UUID mobId = serverContext.getSystemsAdminServer().getUuidEntityManager().getEntityUuid(ia);
                serverContext.getServerConnexion().broadCastPacket(new MobAttackCoolDownPacket(mobId, 15));

                world.edit(ia).create(EnemyAttackCooldownComponent.class).set(15, () -> {
                    Rectangle.tmp.set(playerPositionComponent.x, playerPositionComponent.y, playerBox2dComponent.widthWorld, playerBox2dComponent.heightWorld);
                    Rectangle.tmp2.set(iaPositionComponent.x, iaPositionComponent.y, iaBox2dComponent.widthWorld, iaBox2dComponent.heightWorld);

                    if (Rectangle.tmp.overlaps(Rectangle.tmp2)) {
                        Vector2 playerRectangleCenter = new Vector2();
                        Vector2 iaRectangleCenter = new Vector2();
                        Rectangle.tmp.getCenter(playerRectangleCenter);
                        Rectangle.tmp2.getCenter(iaRectangleCenter);
                        Vector2 knockbackVector = playerRectangleCenter.sub(iaRectangleCenter).nor().setLength(10000);

                        // remove player heal
                        if (playerLifeComponent.decrementHeart(1)) {
//                            playerLifeComponent.set(10);
//                            playerBox2dComponent.body.setTransform(0, 0, 0);
                        } else {
                            // knock back if not death
                            playerBox2dComponent.body.applyForce(knockbackVector, playerBox2dComponent.body.getPosition(), true);
                        }
                    }
                    MessageManager.getInstance().dispatchMessage(null, enemyComponent.getEnemyTelegraph(), EnemyState.FOCUS_PLAYER_MESSAGE, entityId);
                });
            }
        }
    }
}
