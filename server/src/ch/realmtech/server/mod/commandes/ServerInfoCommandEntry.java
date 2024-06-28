package ch.realmtech.server.mod.commandes;

import ch.realmtech.server.mod.commandes.masterCommand.MasterServerCommandNew;
import ch.realmtech.server.registry.CommandEntry;

import java.util.Map;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.ParentCommand;

@Command(name = "server-info", aliases = "info", description = "Show server general info")
public class ServerInfoCommandEntry extends CommandEntry {
    public ServerInfoCommandEntry() {
        super("ServerInfo");
    }

    @ParentCommand
    private MasterServerCommandNew masterServerCommandNew;

    @Override
    public void run() {
        StringBuilder serverInfoStringBuilder = new StringBuilder();
        Map<String, Object> serverInfoMap = masterServerCommandNew.getSystemAdmin().getServerInfoManager().getServerInfoMap();
        for (String key : serverInfoMap.keySet()) {
            Object value = serverInfoMap.get(key);
            serverInfoStringBuilder
                    .append(key)
                    .append(": ")
                    .append(value)
                    .append('\n');
        }
        // already has a new line
        masterServerCommandNew.output.print(serverInfoStringBuilder);
    }
}
