package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ecs.component.Box2dComponent;
import ch.realmtech.server.ecs.component.PositionComponent;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.ia.IaComponent;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;

import java.util.UUID;

public class WeaponRayManager extends Manager {

    @Wire
    private SystemsAdminServer systemsAdminServer;
    @Wire(name = "physicWorld")
    private com.badlogic.gdx.physics.box2d.World physicWorld;

    private ComponentMapper<Box2dComponent> mBox2d;
    private ComponentMapper<PositionComponent> mPos;

    private IntBag rayCast(Vector2 vectorStart, Vector2 vectorEnd, Aspect.Builder aspectBuilder, BodyHitsCallback callback) {
        IntBag entities = world.getAspectSubscriptionManager().get(aspectBuilder).getEntities();

        Bag<Body> bodyHits = new Bag<>();
        physicWorld.rayCast((fixture, point, normal, fraction) -> callback.reportRayFixture(bodyHits, fixture, point, normal, fraction), vectorStart, vectorEnd);

        IntBag entityHits = new IntBag();
        for (int i = 0; i < entities.size(); i++) {
            for (int j = 0; j < bodyHits.size(); j++) {
                int entityId = entities.get(i);
                if (mBox2d.get(entityId).body == bodyHits.get(j)) {
                    entityHits.add(entityId);
                }
            }
        }

        return entityHits;
    }

    public int getMobHit(int playerId, Vector2 vectorEnd) {
        PositionComponent playerPos = mPos.get(playerId);

        IntBag mobs = rayCast(new Vector2(playerPos.x, playerPos.y), vectorEnd, Aspect.all(IaComponent.class), getFirstHit());

        if (!mobs.isEmpty()) {
            return mobs.get(0);
        } else {
            return -1;
        }
    }

    public UUID playerWeaponShot(int playerId, Vector2 vectorEnd) {
        int mobHit = getMobHit(playerId, vectorEnd);
        if (mobHit != -1) {
            UUID mobUuid = systemsAdminServer.getUuidEntityManager().getEntityUuid(mobHit);
            systemsAdminServer.getMobManager().destroyMob(mobHit);
            return mobUuid;
        } else {
            return null;
        }
    }

    private interface BodyHitsCallback {
        float reportRayFixture(Bag<Body> bodyAccumulator, Fixture fixture, Vector2 point, Vector2 normal, float fraction);
    }

    private BodyHitsCallback getFirstHit() {
        return (bodyAccumulator, fixture, point, normal, fraction) -> {
            bodyAccumulator.add(fixture.getBody());
            return 0;
        };
    }
}
