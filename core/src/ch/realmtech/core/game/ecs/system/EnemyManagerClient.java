package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.RealmTech;
import ch.realmtech.core.game.ecs.plugin.SystemsAdminClient;
import ch.realmtech.server.PhysiqueWorldHelper;
import ch.realmtech.server.ecs.component.*;
import ch.realmtech.server.registry.MobEntry;
import com.artemis.ComponentMapper;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class EnemyManagerClient extends Manager {
    private final static Logger logger = LoggerFactory.getLogger(EnemyManagerClient.class);
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
    private ComponentMapper<MobComponent> mMob;

    public void setMob(UUID uuid, float x, float y, MobEntry mobEntry) {
        int entityId = systemsAdminClient.getUuidEntityManager().getEntityId(uuid);
        if (entityId == -1) {
            newCreateEnemy(uuid, x, y, mobEntry);
        } else {
            Box2dComponent box2dComponent = mBox2d.get(entityId);
            box2dComponent.body.setTransform(x + box2dComponent.widthWorld / 2, y + box2dComponent.heightWorld / 2, box2dComponent.body.getAngle());
        }
    }

    private void newCreateEnemy(UUID uuid, float x, float y, MobEntry mobEntry) {
        int enemyId = world.create();
        mobEntry.getMobBehavior().getEditEntity().createEntity(context.getExecuteOnContext(), enemyId);
        Box2dComponent box2dComponent = mBox2d.get(enemyId);
        box2dComponent.body.setTransform(x + box2dComponent.widthWorld / 2, y + box2dComponent.heightWorld / 2, box2dComponent.body.getAngle());
        systemsAdminClient.getUuidEntityManager().registerEntityIdWithUuid(uuid, enemyId);
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

    public void deleteMob(UUID mobUuid) {
        int entityId = systemsAdminClient.getUuidEntityManager().getEntityId(mobUuid);
        if (entityId == -1) {
            logger.warn("Can not find enemy uuid {}", mobUuid);
            return;
        }

        MobComponent mobComponent = mMob.get(entityId);
        mobComponent.getMobEntry().getMobBehavior().getEditEntity().deleteEntity(context.getExecuteOnContext(), entityId);
    }
}
