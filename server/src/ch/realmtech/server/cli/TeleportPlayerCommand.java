package ch.realmtech.server.cli;

import ch.realmtech.server.ecs.component.Box2dComponent;
import com.artemis.ComponentMapper;

import java.util.UUID;
import java.util.concurrent.Callable;

import static picocli.CommandLine.*;

@Command(name = "teleport-player", aliases = "tp", description = "teleport a player to specified coordinate")
public class TeleportPlayerCommand implements Callable<Integer> {
    @ParentCommand
    MasterServerCommand masterCommand;

    @Parameters(index = "0", description = "The uuid of the player")
    private String playerUuid;

    @Parameters(index = "1", description = "X game coordinate")
    private float x;

    @Parameters(index = "2", description = "Y game coordinate")
    private float y;


    @Override
    public Integer call() throws Exception {
        UUID uuid;
        try {
            uuid = UUID.fromString(playerUuid);
        } catch (IllegalArgumentException e) {
            masterCommand.output.println(e.getMessage());
            return -1;
        }

        ComponentMapper<Box2dComponent> mBox2d = masterCommand.getWorld().getMapper(Box2dComponent.class);
        int playerId = masterCommand.serverContext.getSystemsAdminServer().getPlayerManagerServer().getPlayerByUuid(uuid);
        if (playerId == -1) {
            masterCommand.output.println("uuid mismatch, player not found");
            return -1;
        }
        Box2dComponent box2dComponent = mBox2d.get(playerId);
        box2dComponent.body.setTransform(x, y, box2dComponent.body.getAngle());
        return 0;
    }
}
