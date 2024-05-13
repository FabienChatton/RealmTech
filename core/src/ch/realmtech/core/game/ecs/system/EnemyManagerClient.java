package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.RealmTech;
import ch.realmtech.core.game.ecs.component.TextureColorComponent;
import ch.realmtech.core.game.ecs.plugin.SystemsAdminClient;
import ch.realmtech.server.PhysiqueWorldHelper;
import ch.realmtech.server.ecs.component.*;
import ch.realmtech.server.ecs.plugin.forclient.EnemyManagerForClient;
import com.artemis.ComponentMapper;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EnemyManagerClient extends Manager implements EnemyManagerForClient {
    @Wire(name = "context")
    private RealmTech context;
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
    private final Map<UUID, Integer> enemies = new HashMap<>();

    public void otherIa(UUID uuid, float x, float y) {
        if (enemies.containsKey(uuid)) {
            Box2dComponent box2dComponent = mBox2d.get(enemies.get(uuid));
            box2dComponent.body.setTransform(x + box2dComponent.widthWorld / 2, y + box2dComponent.heightWorld / 2, box2dComponent.body.getAngle());
        } else {
            enemies.put(uuid, createEnemy(uuid, x, y));
        }
    }

    private int createEnemy(UUID uuid, float x, float y) {
        int iaTestId = world.create();

        TextureComponent textureComponent = world.edit(iaTestId).create(TextureComponent.class);
        textureComponent.scale = 1.6f;
        TextureAnimationComponent textureAnimationComponent = world.edit(iaTestId).create(TextureAnimationComponent.class);

        TextureAtlas.AtlasRegion textureFront0 = textureAtlas.findRegion("zombie-0");
        TextureAtlas.AtlasRegion textureFront1 = textureAtlas.findRegion("zombie-1");
        TextureAtlas.AtlasRegion textureFront2 = textureAtlas.findRegion("zombie-2");
        textureAnimationComponent.animationFront = new TextureRegion[]{textureFront0, textureFront1, textureFront2};

        PhysiqueWorldHelper.resetBodyDef(bodyDef);
        PhysiqueWorldHelper.resetFixtureDef(fixtureDef);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        Body bodyIaTest = physicWorld.createBody(bodyDef);
        bodyIaTest.setUserData(iaTestId);
        PolygonShape playerShape = new PolygonShape();

        TextureRegion textureRegion = textureAnimationComponent.animationFront[0];
        playerShape.setAsBox(textureRegion.getRegionWidth() / RealmTech.PPM, textureRegion.getRegionHeight() / RealmTech.PPM);
        fixtureDef.shape = playerShape;
        fixtureDef.filter.categoryBits = PhysiqueWorldHelper.BIT_PLAYER;
        fixtureDef.filter.maskBits = PhysiqueWorldHelper.BIT_WORLD | PhysiqueWorldHelper.BIT_GAME_OBJECT;
        bodyIaTest.createFixture(fixtureDef);
        bodyIaTest.setTransform(x, y, bodyIaTest.getAngle());

        playerShape.dispose();
        Box2dComponent box2dComponent = world.edit(iaTestId).create(Box2dComponent.class);
        box2dComponent.set(1, 1, bodyIaTest);
        box2dComponent.body.setTransform(x + box2dComponent.widthWorld / 2, y + box2dComponent.heightWorld / 2, box2dComponent.body.getAngle());
        world.edit(iaTestId).create(PositionComponent.class);
        systemsAdminClient.getUuidEntityManager().registerEntityIdWithUuid(uuid, iaTestId);

        world.edit(iaTestId).create(MouvementComponent.class);

        return iaTestId;
    }

    @Override
    public void enemyJustHit(UUID enemyUuid) {
        int enemyId = systemsAdminClient.getUuidEntityManager().getEntityId(enemyUuid);
        world.edit(enemyId).create(TextureColorComponent.class).set(Color.RED);
        systemsAdminClient.getParticleEffectsSystem().createHitEffect(mBox2d.get(enemyId).body.getPosition());
        context.nextTickSimulation(60, () -> world.edit(enemyId).remove(TextureColorComponent.class));
    }
}
