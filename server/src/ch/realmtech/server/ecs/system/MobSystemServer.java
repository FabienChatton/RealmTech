package ch.realmtech.server.ecs.system;

import ch.realmtech.server.PhysiqueWorldHelper;
import ch.realmtech.server.ServerContext;
import ch.realmtech.server.ecs.component.Box2dComponent;
import ch.realmtech.server.ecs.component.LifeComponent;
import ch.realmtech.server.ecs.component.PositionComponent;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.enemy.EnemyComponent;
import ch.realmtech.server.enemy.EnemyState;
import ch.realmtech.server.enemy.EnemySteerable;
import ch.realmtech.server.enemy.EnemyTelegraph;
import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import java.util.UUID;

public class MobSystemServer extends BaseSystem {
    @Wire(name = "physicWorld")
    private com.badlogic.gdx.physics.box2d.World physicWorld;
    @Wire(name = "serverContext")
    private ServerContext serverContext;
    @Wire
    private FixtureDef fixtureDef;
    @Wire
    private BodyDef bodyDef;
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
        // naturalSpawnIa();
        messageManager.update();
    }

    private void naturalSpawnIa() {
        IntBag iaEntities = world.getAspectSubscriptionManager().get(Aspect.all(EnemyComponent.class)).getEntities();
        IntBag players = systemsAdminServer.getPlayerManagerServer().getPlayers();

        if (iaEntities.isEmpty()) {
            for (int i = 0; i < players.size(); i++) {
                int playerId = players.get(i);
                if (mPos.has(playerId)) {
                    PositionComponent playerPosition = mPos.get(playerId);

                    createMobTest(playerPosition.x + 5, playerPosition.y, playerId);
                }
            }
        }
    }

    public int createMobTest(float x, float y, int playerId) {
        int mobId = world.create();
        PhysiqueWorldHelper.resetBodyDef(bodyDef);
        PhysiqueWorldHelper.resetFixtureDef(fixtureDef);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        Body bodyMob = physicWorld.createBody(bodyDef);
        bodyMob.setUserData(mobId);

        PolygonShape physicContactShape = new PolygonShape();
        physicContactShape.setAsBox(0.9f, 0.9f);
        fixtureDef.shape = physicContactShape;
        fixtureDef.filter.categoryBits = PhysiqueWorldHelper.BIT_GAME_OBJECT;
        fixtureDef.filter.maskBits = PhysiqueWorldHelper.BIT_WORLD | PhysiqueWorldHelper.BIT_GAME_OBJECT;
        bodyMob.createFixture(fixtureDef);
        bodyMob.setTransform(x, y, bodyMob.getAngle());

        PhysiqueWorldHelper.resetFixtureDef(fixtureDef);
        PolygonShape playerContactShape = new PolygonShape();
        playerContactShape.setAsBox(0.1f, 0.1f);
        fixtureDef.shape = playerContactShape;
        fixtureDef.filter.categoryBits = PhysiqueWorldHelper.BIT_GAME_OBJECT;
        fixtureDef.filter.maskBits = PhysiqueWorldHelper.BIT_PLAYER;
        bodyMob.createFixture(fixtureDef);

        playerContactShape.dispose();
        EnemyComponent enemyComponent = world.edit(mobId).create(EnemyComponent.class).set(new EnemyTelegraph(mobId, serverContext), new EnemySteerable(bodyMob, 4));
        messageManager.dispatchMessage(null, enemyComponent.getIaTestAgent(), EnemyState.FOCUS_PLAYER_MESSAGE, playerId);
        //iaComponent.getIaTestSteerable().setSteeringBehavior(new Seek<>(iaComponent.getIaTestSteerable(), new Box2dLocation(target)));
        world.edit(mobId).create(Box2dComponent.class).set(0.9f, 0.9f, bodyMob);
        world.edit(mobId).create(LifeComponent.class).set(10);
        PositionComponent positionComponent = world.edit(mobId).create(PositionComponent.class);
        serverContext.getSystemsAdminServer().getUuidEntityManager().registerEntityIdWithUuid(UUID.randomUUID(), mobId);
        positionComponent.set(x, y);
        return mobId;
    }
}
