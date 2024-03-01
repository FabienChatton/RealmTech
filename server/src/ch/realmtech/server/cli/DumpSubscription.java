package ch.realmtech.server.cli;


import ch.realmtech.server.ecs.plugin.commun.ContextType;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import ch.realmtech.server.ecs.system.PlayerManagerServer;
import ch.realmtech.server.ecs.system.PlayerSubscriptionSystem;

import java.util.List;
import java.util.UUID;

import static picocli.CommandLine.*;

@Command(name = "subscription", description = "dump all subscription", mixinStandardHelpOptions = true)
public class DumpSubscription implements Runnable {
    @ParentCommand
    private DumpCommand dumpCommand;

    @Parameters(index = "0", description = "The player identifier, username or uuid")
    private String playerIdentifieur;

    @Override
    public void run() {
        SystemsAdminCommun systemsAdminCommun = dumpCommand.masterCommand.getWorld().getRegistered("systemsAdmin");
        systemsAdminCommun.onContextType(ContextType.CLIENT, () -> dumpCommand.printlnVerbose(0, "Sorry only available on server"));
        systemsAdminCommun.onContextType(ContextType.SERVER, () -> {
            int playerId = dumpCommand.masterCommand.getWorld().getSystem(PlayerManagerServer.class).getPlayerByUsernameOrUuid(playerIdentifieur);
            if (playerId == -1) {
                dumpCommand.printlnVerbose(0, "Invalide player username or uuid: " + playerIdentifieur);
                return;
            }

            List<UUID> subscriptionForPlayer = dumpCommand.masterCommand.getWorld().getSystem(PlayerSubscriptionSystem.class).getSubscriptionForPlayer(playerId);
            dumpCommand.printlnVerbose(0, subscriptionForPlayer.toString());
        });
    }
}
