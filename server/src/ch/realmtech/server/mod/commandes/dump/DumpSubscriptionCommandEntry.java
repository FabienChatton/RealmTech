package ch.realmtech.server.mod.commandes.dump;

import ch.realmtech.server.ecs.plugin.commun.ContextType;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import ch.realmtech.server.registry.CommandEntry;

import java.util.List;
import java.util.UUID;

import static picocli.CommandLine.*;

@Command(name = "subscription", description = "dump all subscription", mixinStandardHelpOptions = true)
public class DumpSubscriptionCommandEntry extends CommandEntry {
    public DumpSubscriptionCommandEntry() {
        super("DumpSubscription");
    }

    @ParentCommand
    public DumpCommandEntry dumpCommand;

    @Parameters(index = "0", description = "The player identifier, username or uuid")
    private String playerIdentifieur;

    @Override
    public void run() {
        SystemsAdminCommun systemsAdminCommun = dumpCommand.masterCommand.getWorld().getRegistered("systemsAdmin");
        // TODO ajouter un test lors de initialization pour server side only quand parent est dans common
        systemsAdminCommun.onContextType(ContextType.CLIENT, () -> dumpCommand.printlnVerbose(0, "Sorry only available on server"));
        systemsAdminCommun.onContextType(ContextType.SERVER, () -> {
            dumpCommand.masterCommand.getContext().getExecuteOnContext().onServer((systemsAdminServer, serverContext) -> {
                int playerId = systemsAdminServer.getPlayerManagerServer().getPlayerByUsernameOrUuid(playerIdentifieur);
                if (playerId == -1) {
                    dumpCommand.printlnVerbose(0, "Invalide player username or uuid: " + playerIdentifieur);
                    return;
                }

                List<UUID> subscriptionForPlayer = serverContext.getSystemsAdminServer().getPlayerSubscriptionSystem().getSubscriptionForPlayer(playerId);
                dumpCommand.printlnVerbose(0, subscriptionForPlayer.toString());
            });
        });
    }
}
