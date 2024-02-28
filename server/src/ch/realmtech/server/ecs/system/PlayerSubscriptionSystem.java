package ch.realmtech.server.ecs.system;

import ch.realmtech.server.divers.Position;
import ch.realmtech.server.ecs.component.PlayerConnexionComponent;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import com.artemis.ComponentMapper;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.artemis.utils.ImmutableIntBag;
import com.artemis.utils.IntBag;

import java.util.List;
import java.util.UUID;

public class PlayerSubscriptionSystem extends Manager {
    @Wire
    private SystemsAdminServer systemsAdminServer;
    private ComponentMapper<PlayerConnexionComponent> mPlayerConnexion;

    public void addEntityIdSubscriptionToPlayer(int playerId, UUID entityUuidSubscription) {
        int entityId = systemsAdminServer.uuidEntityManager.getEntityId(entityUuidSubscription);
        if (entityId == -1) return;
        addEntityIdSubscriptionToPlayer(playerId, entityId);
    }
    public void addEntityIdSubscriptionToPlayer(int playerId, int entityIdSubscription) {
        mPlayerConnexion.get(playerId).entitySubscription.add(entityIdSubscription);
    }

    public void removeEntityIdSubscriptionToPlayer(int playerId, UUID entityUuidSubscription) {
        int entityId = systemsAdminServer.uuidEntityManager.getEntityId(entityUuidSubscription);
        if (entityId == -1) return;
        removeEntityIdSubscriptionToPlayer(playerId, entityId);
    }

    public void removeEntityIdSubscriptionToPlayer(int playerId, int entityIdSubscription) {
        mPlayerConnexion.get(playerId).entitySubscription.removeValue(entityIdSubscription);
    }

    public ImmutableIntBag<?> getPlayersInRangeForChunkPos(Position chunkPos) {
        IntBag players = systemsAdminServer.playerManagerServer.getPlayers();
        int[] playersData = players.getData();
        IntBag playersSubscription = new IntBag(Math.min(players.size(), 64));

        players:
        for (int i = 0; i < players.size(); i++) {
            int playerId = playersData[i];
            List<Position> chunkPoss = mPlayerConnexion.get(playerId).chunkPoss;
            if (chunkPoss.contains(chunkPos)) {
                playersSubscription.add(playerId);
            }
        }

        return playersSubscription;
    }

    public ImmutableIntBag<?> getPlayerForEntityIdSubscription(int entityIdSubscription) {
        IntBag players = systemsAdminServer.playerManagerServer.getPlayers();
        int[] playersData = players.getData();
        IntBag playersSubscription = new IntBag(Math.min(players.size(), 64));

        players:
        for (int i = 0; i < players.size(); i++) {
            int playerId = playersData[i];
            IntBag playerEntitySubscription = mPlayerConnexion.get(playerId).entitySubscription;
            int[] playerEntitySubscriptionData = playerEntitySubscription.getData();
            for (int j = 0; j < playerEntitySubscription.size(); j++) {
                int playerEntityIDSubscription = playerEntitySubscriptionData[i];
                if (playerEntityIDSubscription == entityIdSubscription) {
                    playersSubscription.add(playerId);
                    continue players;
                }
            }
        }
        return playersSubscription;
    }
}
