package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ecs.component.Box2dComponent;
import ch.realmtech.server.ecs.component.LifeComponent;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import com.artemis.ComponentMapper;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.math.Vector2;

public class EnemyManager extends Manager {
    @Wire(name = "physicWorld")
    private com.badlogic.gdx.physics.box2d.World physicWorld;
    @Wire(name = "systemsAdmin")
    private SystemsAdminCommun systemsAdmin;
    private ComponentMapper<Box2dComponent> mBox2d;
    private ComponentMapper<LifeComponent> mLife;

    private final MessageManager messageManager = MessageManager.getInstance();

    /**
     * Destroy enemy from the world, ecs and physic.
     * If on server, prefer to {@link ch.realmtech.server.enemy.EnemySystem#destroyEnemyServer(int)}
     * to send a packet to player.
     *
     * @param enemyId The enemy to delete.
     */
    public void destroyWorldEnemy(int enemyId) {
        Box2dComponent box2dComponent = mBox2d.get(enemyId);
        physicWorld.destroyBody(box2dComponent.body);
        world.delete(enemyId);
    }

    public void knockBackMob(int mobId, Vector2 knockBackAmount) {
        Box2dComponent box2dComponent = mBox2d.get(mobId);
        box2dComponent.body.applyLinearImpulse(knockBackAmount, box2dComponent.body.getWorldCenter(), true);
    }
}
