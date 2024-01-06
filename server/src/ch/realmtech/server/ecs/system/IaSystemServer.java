package ch.realmtech.server.ecs.system;

import ch.realmtech.server.PhysiqueWorldHelper;
import ch.realmtech.server.ServerContext;
import ch.realmtech.server.ecs.component.Box2dComponent;
import ch.realmtech.server.ecs.component.PositionComponent;
import ch.realmtech.server.ecs.component.UuidComponent;
import ch.realmtech.server.ia.IaComponent;
import ch.realmtech.server.ia.IaTestSteerable;
import ch.realmtech.server.ia.IaTestTelegraph;
import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.physics.box2d.*;

import java.util.UUID;

public class IaSystemServer extends BaseSystem {
    @Wire(name = "serverContext")
    private ServerContext serverContext;
    @Wire(name = "physicWorld")
    private World physicWorld;
    @Wire
    private FixtureDef fixtureDef;
    @Wire
    private BodyDef bodyDef;
    private final MessageManager messageManager = MessageManager.getInstance();

    @Override
    protected void processSystem() {
        messageManager.update();
    }

    public int createIaTest(int x, int y) {
        int iaTestId = world.create();
        PhysiqueWorldHelper.resetBodyDef(bodyDef);
        PhysiqueWorldHelper.resetFixtureDef(fixtureDef);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        Body bodyIaTest = physicWorld.createBody(bodyDef);
        bodyIaTest.setUserData(iaTestId);
        PolygonShape playerShape = new PolygonShape();
        playerShape.setAsBox(iaTestId / 2f, iaTestId / 2f);
        fixtureDef.shape = playerShape;
        fixtureDef.filter.categoryBits = PhysiqueWorldHelper.BIT_PLAYER;
        fixtureDef.filter.maskBits = PhysiqueWorldHelper.BIT_WORLD | PhysiqueWorldHelper.BIT_GAME_OBJECT;
        bodyIaTest.createFixture(fixtureDef);
        bodyIaTest.setTransform(x, y, bodyIaTest.getAngle());

        playerShape.dispose();
        IaComponent iaComponent = world.edit(iaTestId).create(IaComponent.class).set(new IaTestTelegraph(iaTestId, serverContext), new IaTestSteerable(bodyIaTest, 4));
        // iaComponent.getIaTestSteerable().setSteeringBehavior(new Seek<>(iaComponent.getIaTestSteerable(), new Box2dLocation(box2dComponentPlayer.body)));
        world.edit(iaTestId).create(Box2dComponent.class).set(1, 1, bodyIaTest);
        PositionComponent positionComponent = world.edit(iaTestId).create(PositionComponent.class);
        world.edit(iaTestId).create(UuidComponent.class).set(UUID.randomUUID());
        positionComponent.set(x, y);
        return iaTestId;
    }
}
