package ch.realmtech.server.ecs.system;

import ch.realmtech.server.divers.Position;
import ch.realmtech.server.ecs.component.PlayerConnexionComponent;
import ch.realmtech.server.ecs.component.PositionComponent;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableIntBag;
import com.artemis.utils.IntBag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class PlayerSubscriptionSystem extends Manager {
    private final static Logger logger = LoggerFactory.getLogger(PlayerSubscriptionSystem.class);
    @Wire
    private SystemsAdminServer systemsAdminServer;
    private ComponentMapper<PlayerConnexionComponent> mPlayerConnexion;
    private ComponentMapper<PositionComponent> mPos;

    public boolean addEntitySubscriptionToPlayer(int playerId, UUID entityUuidSubscription) {
        PlayerConnexionComponent playerConnexionComponent = mPlayerConnexion.get(playerId);
        if (playerConnexionComponent.entitySubscription.contains(entityUuidSubscription)) {
            logger.trace("Player {} try to subscribe to an already subscribed entity: {}", playerConnexionComponent.getUsername(), entityUuidSubscription);
            return false;
        }
        playerConnexionComponent.entitySubscription.add(entityUuidSubscription);
        return true;
    }

    public boolean removeEntitySubscriptionToPlayer(int playerId, UUID entityUuidSubscription) {
        PlayerConnexionComponent playerConnexionComponent = mPlayerConnexion.get(playerId);
        if (!playerConnexionComponent.entitySubscription.contains(entityUuidSubscription)) {
            logger.trace("Player {} try to unSubscribe to unSubscribed entity: {}", playerConnexionComponent.getUsername(), entityUuidSubscription);
            return false;
        }
        playerConnexionComponent.entitySubscription.remove(entityUuidSubscription);
        return true;
    }

    public ImmutableIntBag<?> getPlayersInRangeForChunkPos(Position chunkPos) {
        return getEntityInRangeForChunkPos(chunkPos, Aspect.all(PositionComponent.class, PlayerConnexionComponent.class));
    }

    public ImmutableIntBag<?> getEntityInRangeForChunkPos(Position chunkPos, Aspect.Builder aspectBuilder) {
        IntBag entities = world.getAspectSubscriptionManager().get(aspectBuilder).getEntities();
        int[] entitiesData = entities.getData();
        IntBag subscription = new IntBag(Math.min(entities.size(), 64));

        for (int i = 0; i < entities.size(); i++) {
            int entityId = entitiesData[i];
            PositionComponent entityPos = mPos.get(entityId);
            int entityChunkPosX = MapManager.getChunkPos(MapManager.getWorldPos(entityPos.x));
            int entityChunkPosY = MapManager.getChunkPos(MapManager.getWorldPos(entityPos.y));
            if (systemsAdminServer.getMapSystemServer().chunkEstDansLaRenderDistance(chunkPos, entityChunkPosX, entityChunkPosY)) {
                subscription.add(entityId);
            }
        }

        return subscription;
    }

    public ImmutableIntBag<?> getPlayerForEntityIdSubscription(UUID entityIdSubscription) {
        IntBag players = systemsAdminServer.getPlayerManagerServer().getPlayers();
        int[] playersData = players.getData();
        IntBag playersSubscription = new IntBag(Math.min(players.size(), 64));

        players:
        for (int i = 0; i < players.size(); i++) {
            int playerId = playersData[i];
            Bag<UUID> playerEntitySubscription = mPlayerConnexion.get(playerId).entitySubscription;
            Iterator<UUID> uuidIterator = playerEntitySubscription.iterator();
            while (uuidIterator.hasNext()) {
                UUID playerEntityIDSubscription = uuidIterator.next();
                if (playerEntityIDSubscription.equals(entityIdSubscription)) {
                    playersSubscription.add(playerId);
                    continue players;
                }
            }
        }
        return playersSubscription;
    }

    public List<UUID> getSubscriptionForPlayer(int playerId) {
        List<UUID> uuids = new ArrayList<>();
        if (!mPlayerConnexion.has(playerId)) {
            throw new NoSuchElementException("entityId: " + playerId + " don't have subscription component");
        }

        PlayerConnexionComponent playerConnexionComponent = mPlayerConnexion.get(playerId);
        Bag<UUID> entitySubscription = playerConnexionComponent.entitySubscription;
        for (int i = 0; i < entitySubscription.size(); i++) {
            UUID entityUuid = entitySubscription.get(i);
            uuids.add(entityUuid);
        }

        return uuids;
    }

    public boolean playerRequestSubscriptionToEntity(int playerId, UUID entityUuid) {
        PositionComponent playerPos = mPos.get(playerId);
        int entityId = systemsAdminServer.getUuidEntityManager().getEntityId(entityUuid);
        if (mPos.has(entityId)) {
            int playerChunkPosX = MapManager.getChunkPos(MapManager.getWorldPos(playerPos.x));
            int playerChunkPosY = MapManager.getChunkPos(MapManager.getWorldPos(playerPos.y));

            PositionComponent entityPos = mPos.get(entityId);
            int entityChunkPosX = MapManager.getChunkPos(MapManager.getWorldPos(entityPos.x));
            int entityChunkPosY = MapManager.getChunkPos(MapManager.getWorldPos(entityPos.y));
            return systemsAdminServer.getMapSystemServer().chunkEstDansLaRenderDistance(new Position(playerChunkPosX, playerChunkPosY), entityChunkPosX, entityChunkPosY);
        } else {
            // ajouter sécurité pour distance avec inventaire
            return true;
        }
    }
}
