package ch.realmtech.server.ecs.system;

import ch.realmtech.server.divers.Position;
import ch.realmtech.server.ecs.component.PlayerConnexionComponent;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import com.artemis.ComponentMapper;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableIntBag;
import com.artemis.utils.IntBag;

import java.util.*;

public class PlayerSubscriptionSystem extends Manager {
    @Wire
    private SystemsAdminServer systemsAdminServer;
    private ComponentMapper<PlayerConnexionComponent> mPlayerConnexion;

    public void addEntitySubscriptionToPlayer(int playerId, UUID entityIdSubscription) {
        mPlayerConnexion.get(playerId).entitySubscription.add(entityIdSubscription);
    }

    public void removeEntitySubscriptionToPlayer(int playerId, UUID entityUuidSubscription) {
        mPlayerConnexion.get(playerId).entitySubscription.remove(entityUuidSubscription);
    }

    public ImmutableIntBag<?> getPlayersInRangeForChunkPos(Position chunkPos) {
        IntBag players = systemsAdminServer.getPlayerManagerServer().getPlayers();
        int[] playersData = players.getData();
        IntBag playersSubscription = new IntBag(Math.min(players.size(), 64));

        for (int i = 0; i < players.size(); i++) {
            int playerId = playersData[i];
            List<Position> chunkPoss = mPlayerConnexion.get(playerId).chunkPoss;
            if (chunkPoss.contains(chunkPos)) {
                playersSubscription.add(playerId);
            }
        }

        return playersSubscription;
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
}
