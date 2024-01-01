package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.game.ecs.plugin.SystemsAdminClient;
import ch.realmtech.server.PhysiqueWorldHelper;
import ch.realmtech.server.ecs.component.Box2dComponent;
import ch.realmtech.server.ecs.component.PositionComponent;
import ch.realmtech.server.ecs.component.TextureComponent;
import ch.realmtech.server.ecs.component.UuidComponent;
import ch.realmtech.server.ia.IaComponent;
import ch.realmtech.server.ia.IaTestSteerable;
import ch.realmtech.server.ia.IaTestTelegraph;
import com.artemis.ComponentMapper;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class IaManagerClient extends Manager {
    @Wire
    private SystemsAdminClient systemsAdminClient;
    @Wire
    private TextureAtlas textureAtlas;
    @Wire(name = "physicWorld")
    private World physicWorld;
    @Wire
    private FixtureDef fixtureDef;
    @Wire
    private BodyDef bodyDef;
    private ComponentMapper<Box2dComponent> mBox2d;
    private final Map<UUID, Integer> iaS = new HashMap<>();

    public void otherIa(UUID uuid, float x, float y) {
        if (iaS.containsKey(uuid)) {
            Box2dComponent box2dComponent = mBox2d.get(iaS.get(uuid));
            box2dComponent.body.setTransform(x + box2dComponent.widthWorld / 2, y + box2dComponent.heightWorld / 2, box2dComponent.body.getAngle());
        } else {
            iaS.put(uuid, createIa(uuid, x, y));
        }
    }

    private int createIa(UUID uuid, float x, float y) {
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
        world.edit(iaTestId).create(IaComponent.class).set(new IaTestTelegraph(), new IaTestSteerable(bodyIaTest, 4));
        Box2dComponent box2dComponent = world.edit(iaTestId).create(Box2dComponent.class);
        box2dComponent.set(1, 1, bodyIaTest);
        box2dComponent.body.setTransform(x + box2dComponent.widthWorld / 2, y + box2dComponent.heightWorld / 2, box2dComponent.body.getAngle());
        world.edit(iaTestId).create(PositionComponent.class);
        world.edit(iaTestId).create(UuidComponent.class).set(uuid);
        TextureComponent textureComponent = world.edit(iaTestId).create(TextureComponent.class);
        textureComponent.set(textureAtlas.findRegion("sandales-01"));
        return iaTestId;
    }
}
