package ch.realmtechServer.cli;


import ch.realmtechServer.ecs.component.PlayerConnexionComponent;
import ch.realmtechServer.ecs.component.PositionComponent;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.utils.IntBag;

import java.util.concurrent.Callable;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.ParentCommand;

@Command(name = "players", description = "dump all loaded players")
public class DumpPlayersCommand implements Callable<Integer> {
    @ParentCommand
    DumpCommand dumpCommand;
    @Override
    public Integer call() throws Exception {
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
            if (playerConnexionComponent.channel != null) {
                // sur le serveur
                dumpCommand.masterCommand.output.println(
                        String.format("x: %f, y: %f, uuid: %s, ip: %s", positionComponent.x, positionComponent.y, playerConnexionComponent.uuid, playerConnexionComponent.channel.remoteAddress())
                );
            } else {
                // sur le client
                dumpCommand.masterCommand.output.println(
                        String.format("x: %f, y: %f, uuid: %s", positionComponent.x, positionComponent.y, playerConnexionComponent.uuid)
                );
            }
        }
        if (playerEntities.size() == 0) dumpCommand.masterCommand.output.println("No players loaded");
        return 0;
    }
}
