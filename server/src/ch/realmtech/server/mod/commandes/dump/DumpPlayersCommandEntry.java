package ch.realmtech.server.mod.commandes.dump;

import ch.realmtech.server.ecs.component.PlayerConnexionComponent;
import ch.realmtech.server.ecs.component.PositionComponent;
import ch.realmtech.server.ecs.system.UuidEntityManager;
import ch.realmtech.server.registry.CommandEntry;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.utils.IntBag;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.ParentCommand;

@Command(name = "players", description = "dump all loaded players")
public class DumpPlayersCommandEntry extends CommandEntry {
    public DumpPlayersCommandEntry() {
        super("DumpPlayers");
    }

    @ParentCommand
    public DumpCommandEntry dumpCommand;

    @Override
    public void run() {
        ComponentMapper<PositionComponent> mPos = dumpCommand.masterCommand.getWorld().getMapper(PositionComponent.class);
        ComponentMapper<PlayerConnexionComponent> mPlayerConnexion = dumpCommand.masterCommand.getWorld().getMapper(PlayerConnexionComponent.class);
        IntBag playerEntities = dumpCommand.masterCommand.getWorld().getAspectSubscriptionManager().get(Aspect.all(
                PlayerConnexionComponent.class
        )).getEntities();
        int[] data = playerEntities.getData();
        for (int i = 0; i < playerEntities.size(); i++) {
            int playerId = data[i];
            PositionComponent positionComponent = mPos.get(playerId);
            PlayerConnexionComponent playerConnexionComponent = mPlayerConnexion.get(playerId);
            UuidEntityManager uuidEntityManager = dumpCommand.masterCommand.getSystemAdmin().getUuidEntityManager();
            if (playerConnexionComponent.channel != null) {
                // sur le serveur
                dumpCommand.printlnVerbose(1,
                        String.format("x: %f, y: %f, uuid: %s, ip: %s", positionComponent.x, positionComponent.y, uuidEntityManager.getEntityUuid(playerId), playerConnexionComponent.channel.remoteAddress())
                );
            } else {
                // sur le client
                dumpCommand.printlnVerbose(1,
                        String.format("x: %f, y: %f, uuid: %s", positionComponent.x, positionComponent.y, uuidEntityManager.getEntityUuid(playerId))
                );
            }
        }
        if (playerEntities.isEmpty()) dumpCommand.masterCommand.output.println("No players loaded");
        else dumpCommand.masterCommand.output.println("players count: " + playerEntities.size());
    }
}
