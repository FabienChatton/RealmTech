package ch.realmtech.server.cli;


import ch.realmtech.server.divers.Notify;
import ch.realmtech.server.ecs.component.PlayerConnexionComponent;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.packet.clientPacket.NotifySendPacket;

import static picocli.CommandLine.*;

@Command(name = "notify", description = "Notify a player")
public class NotifyServerCommand implements Runnable {
    @ParentCommand
    private MasterServerCommand masterCommand;

    @Parameters(index = "0", description = "The player identifier, username or uuid")
    private String playerIdentifier;

    @Parameters(index = "1", description = "Message to send")
    private String message;


    @Override
    public void run() {
        SystemsAdminServer systemsAdminServer = masterCommand.serverContext.getSystemsAdminServer();
        int playerId = systemsAdminServer.getPlayerManagerServer().getPlayerByUsernameOrUuid(playerIdentifier);

        if (playerId == -1) {
            masterCommand.output.println("Invalide player");
            return;
        }

        PlayerConnexionComponent playerConnexion = systemsAdminServer.getPlayerManagerServer().getPlayerConnexionComponentById(playerId);

        if (playerConnexion == null) {
            masterCommand.output.println("Invalide player connexion");
            return;
        }

        if (playerIdentifier == null) {
            masterCommand.output.println("Invalide message");
            return;
        }

        Notify notify = new Notify("Notify send by " + masterCommand.getSenderString(), message, 2);
        masterCommand.serverContext.getServerConnexion().sendPacketTo(new NotifySendPacket(masterCommand.serverContext, notify), playerConnexion.channel);
    }
}
