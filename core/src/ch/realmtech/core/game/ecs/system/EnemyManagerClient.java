package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.RealmTech;
import ch.realmtech.core.game.ecs.plugin.SystemsAdminClient;
import ch.realmtech.server.ecs.component.Box2dComponent;
import ch.realmtech.server.ecs.component.MobComponent;
import ch.realmtech.server.registry.MobEntry;
import com.artemis.ComponentMapper;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
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
            createEnemy(uuid, x, y, mobEntry);
        } else {
            Box2dComponent box2dComponent = mBox2d.get(entityId);
            box2dComponent.body.setTransform(x + box2dComponent.widthWorld / 2, y + box2dComponent.heightWorld / 2, box2dComponent.body.getAngle());
        }
    }

    private void createEnemy(UUID uuid, float x, float y, MobEntry mobEntry) {
        int enemyId = world.create();
        mobEntry.getMobBehavior().getEditEntity().createEntity(context.getExecuteOnContext(), enemyId);
        Box2dComponent box2dComponent = mBox2d.get(enemyId);
        box2dComponent.body.setTransform(x + box2dComponent.widthWorld / 2, y + box2dComponent.heightWorld / 2, box2dComponent.body.getAngle());
        systemsAdminClient.getUuidEntityManager().registerEntityIdWithUuid(uuid, enemyId);
    }

    public void deleteMob(UUID mobUuid) {
        logger.trace("Delete mob {}", mobUuid);
        int entityId = systemsAdminClient.getUuidEntityManager().getEntityId(mobUuid);
        if (entityId == -1) {
            logger.debug("Can not find enemy uuid {}", mobUuid);
            return;
        }

        MobComponent mobComponent = mMob.get(entityId);
        mobComponent.getMobEntry().getMobBehavior().getEditEntity().deleteEntity(context.getExecuteOnContext(), entityId);
    }
}
