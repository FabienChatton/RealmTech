package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.ecs.component.*;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.enemy.EnemyComponent;
import ch.realmtech.server.packet.clientPacket.EntityHitPacket;
import ch.realmtech.server.packet.clientPacket.ParticleAddPacket;
import ch.realmtech.server.packet.clientPacket.PlayerHasWeaponShotPacket;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class WeaponRayManager extends Manager {
    private final static Logger logger = LoggerFactory.getLogger(WeaponRayManager.class);
    @Wire(name = "serverContext")
    private ServerContext serverContext;
    @Wire
    private SystemsAdminServer systemsAdminServer;
    @Wire(name = "physicWorld")
    private com.badlogic.gdx.physics.box2d.World physicWorld;

    private ComponentMapper<Box2dComponent> mBox2d;
    private ComponentMapper<PositionComponent> mPos;
    private ComponentMapper<ItemComponent> mItem;
    private ComponentMapper<LifeComponent> mLife;

    private IntBag rayCast(Vector2 vectorStart, Vector2 vectorEnd, Aspect.Builder aspectBuilder, BodyHitsCallback callback) {
        IntBag entities = world.getAspectSubscriptionManager().get(aspectBuilder.all(Box2dComponent.class).exclude(InvincibilityComponent.class)).getEntities();

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

    public int getHit(int playerId, Vector2 vectorClick, float range) {
        PositionComponent playerPos = mPos.get(playerId);

        Vector2 vectorStart = playerPos.toVector2().add(0.5f, 0.5f);
        Vector2 vectorEnd = vectorClick.cpy().sub(vectorStart.cpy()).setLength(range).add(vectorStart);
        IntBag entities = rayCast(vectorStart, vectorEnd, Aspect
                .all(LifeComponent.class, PositionComponent.class)
                .one(EnemyComponent.class, PlayerConnexionComponent.class), getFirstHit());

        if (!entities.isEmpty()) {
            return entities.get(0);
        } else {
            return -1;
        }
    }

    public void playerWeaponShot(Channel clientChannel, Vector2 vectorClick, UUID itemUuid) {
        int playerId = serverContext.getSystemsAdminServer().getPlayerManagerServer().getPlayerByChannel(clientChannel);
        UUID playerUuid = systemsAdminServer.getUuidEntityManager().getEntityUuid(playerId);
        PositionComponent playerPos = mPos.get(playerId);

        int itemId = systemsAdminServer.getUuidEntityManager().getEntityId(itemUuid);
        if (itemId == -1) {
            logger.info("Item uuid {} not found for player weapon shot", itemUuid);
            return;
        }

        ItemComponent itemComponent = mItem.get(itemId);

        int chunkPosX = MapManager.getChunkPos(MapManager.getWorldPos(playerPos.x));
        int chunkPosY = MapManager.getChunkPos(MapManager.getWorldPos(playerPos.y));

        if (itemComponent.itemRegisterEntry.getItemBehavior().isFireArme()) {
            serverContext.getServerConnexion().sendPacketToSubscriberForChunkPos(new PlayerHasWeaponShotPacket(playerUuid), chunkPosX, chunkPosY);
        }

        int entityId = getHit(playerId, vectorClick, itemComponent.itemRegisterEntry.getItemBehavior().getAttackRange());
        if (entityId != -1) {
            PositionComponent pos = mPos.get(entityId);
            LifeComponent lifeComponent = mLife.get(entityId);
            UUID EntityUuid = systemsAdminServer.getUuidEntityManager().getEntityUuid(entityId);
            serverContext.getServerConnexion().sendPacketTo(new ParticleAddPacket(ParticleAddPacket.Particles.HIT, pos.toVector2()), clientChannel);

            if (!lifeComponent.decrementHeart(itemComponent.itemRegisterEntry.getItemBehavior().getAttackDommage())) {
                Vector2 knowBack = pos.toVector2().sub(playerPos.toVector2()).setLength2(100);
                systemsAdminServer.getMobManager().knockBackMob(entityId, knowBack);
                world.edit(entityId).create(InvincibilityComponent.class).set(60);
                serverContext.getServerConnexion().sendPacketToSubscriberForChunkPos(new EntityHitPacket(EntityUuid), chunkPosX, chunkPosY);
            }
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
