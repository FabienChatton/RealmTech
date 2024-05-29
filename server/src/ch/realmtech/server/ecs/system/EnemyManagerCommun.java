package ch.realmtech.server.ecs.system;

import ch.realmtech.server.PhysiqueWorldHelper;
import ch.realmtech.server.ecs.component.Box2dComponent;
import ch.realmtech.server.ecs.component.LifeComponent;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import com.artemis.ComponentMapper;
import com.artemis.EntityEdit;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class EnemyManagerCommun extends Manager {
    @Wire(name = "physicWorld")
    private com.badlogic.gdx.physics.box2d.World physicWorld;
    @Wire(name = "systemsAdmin")
    private SystemsAdminCommun systemsAdmin;
    @Wire
    private FixtureDef fixtureDef;
    @Wire
    private BodyDef bodyDef;

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

    public Body createEnemyBody(EntityEdit entityEdit) {
        PhysiqueWorldHelper.resetBodyDef(bodyDef);
        PhysiqueWorldHelper.resetFixtureDef(fixtureDef);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        Body bodyMob = physicWorld.createBody(bodyDef);
        bodyMob.setUserData(entityEdit.getEntityId());

        PolygonShape physicContactShape = new PolygonShape();
        physicContactShape.setAsBox(0.9f, 0.9f);
        fixtureDef.shape = physicContactShape;
        fixtureDef.filter.categoryBits = PhysiqueWorldHelper.BIT_GAME_OBJECT;
        fixtureDef.filter.maskBits = PhysiqueWorldHelper.BIT_WORLD | PhysiqueWorldHelper.BIT_GAME_OBJECT;
        bodyMob.createFixture(fixtureDef);

        PhysiqueWorldHelper.resetFixtureDef(fixtureDef);
        PolygonShape playerContactShape = new PolygonShape();
        playerContactShape.setAsBox(0.1f, 0.1f);
        fixtureDef.shape = playerContactShape;
        fixtureDef.filter.categoryBits = PhysiqueWorldHelper.BIT_GAME_OBJECT;
        fixtureDef.filter.maskBits = PhysiqueWorldHelper.BIT_PLAYER;
        bodyMob.createFixture(fixtureDef);

        playerContactShape.dispose();
        Box2dComponent box2dComponent = entityEdit.create(Box2dComponent.class);
        box2dComponent.set(0.9f, 0.9f, bodyMob);
        return bodyMob;
    }
}
