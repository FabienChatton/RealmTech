package ch.realmtech.server.ecs.system;

import ch.realmtech.server.PhysiqueWorldHelper;
import ch.realmtech.server.ServerContext;
import ch.realmtech.server.ecs.component.Box2dComponent;
import ch.realmtech.server.ecs.component.PositionComponent;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.ia.IaComponent;
import ch.realmtech.server.ia.IaTestState;
import ch.realmtech.server.ia.IaTestSteerable;
import ch.realmtech.server.ia.IaTestTelegraph;
import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.physics.box2d.*;

import java.util.UUID;

public class IaSystemServer extends BaseSystem {
    @Wire
    private SystemsAdminServer systemsAdminServer;
    @Wire(name = "serverContext")
    private ServerContext serverContext;
    @Wire(name = "physicWorld")
    private World physicWorld;
    @Wire
    private FixtureDef fixtureDef;
    @Wire
    private BodyDef bodyDef;
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

    public int createIaTest(float x, float y, int playerId) {
        int iaTestId = world.create();
        PhysiqueWorldHelper.resetBodyDef(bodyDef);
        PhysiqueWorldHelper.resetFixtureDef(fixtureDef);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        Body bodyIaTest = physicWorld.createBody(bodyDef);
        bodyIaTest.setUserData(iaTestId);
        PolygonShape playerShape = new PolygonShape();
        playerShape.setAsBox(0.9f, 0.9f);
        fixtureDef.shape = playerShape;
        fixtureDef.filter.categoryBits = PhysiqueWorldHelper.BIT_GAME_OBJECT;
        fixtureDef.filter.maskBits = PhysiqueWorldHelper.BIT_WORLD | PhysiqueWorldHelper.BIT_GAME_OBJECT | PhysiqueWorldHelper.BIT_PLAYER;
        bodyIaTest.createFixture(fixtureDef);
        bodyIaTest.setTransform(x, y, bodyIaTest.getAngle());

        playerShape.dispose();
        IaComponent iaComponent = world.edit(iaTestId).create(IaComponent.class).set(new IaTestTelegraph(iaTestId, serverContext), new IaTestSteerable(bodyIaTest, 4));
        messageManager.dispatchMessage(null, iaComponent.getIaTestAgent(), IaTestState.FOCUS_PLAYER_MESSAGE, playerId);
        //iaComponent.getIaTestSteerable().setSteeringBehavior(new Seek<>(iaComponent.getIaTestSteerable(), new Box2dLocation(target)));
        world.edit(iaTestId).create(Box2dComponent.class).set(1, 1, bodyIaTest);
        PositionComponent positionComponent = world.edit(iaTestId).create(PositionComponent.class);
        serverContext.getSystemsAdmin().uuidEntityManager.registerEntityIdWithUuid(UUID.randomUUID(), iaTestId);
        positionComponent.set(x, y);
        return iaTestId;
    }

    private void naturalSpawnIa() {
        IntBag iaEntities = world.getAspectSubscriptionManager().get(Aspect.all(IaComponent.class)).getEntities();
        IntBag players = systemsAdminServer.playerManagerServer.getPlayers();

        if (iaEntities.isEmpty()) {
            for (int i = 0; i < players.size(); i++) {
                int playerId = players.get(i);
                PositionComponent playerPosition = mPos.get(playerId);

                createIaTest(playerPosition.x + 5, playerPosition.y, playerId);
            }
        }
    }
}
