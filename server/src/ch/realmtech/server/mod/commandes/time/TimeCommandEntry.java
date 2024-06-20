package ch.realmtech.server.mod.commandes.time;

import ch.realmtech.server.mod.commandes.masterCommand.MasterServerCommandNew;
import ch.realmtech.server.registry.CommandEntry;
import picocli.CommandLine;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.ParentCommand;

@Command(name = "time", description = "Master command for time manipulation, see sub command for more details.")
public class TimeCommandEntry extends CommandEntry {
    public TimeCommandEntry() {
        super("Time");
    }

    @ParentCommand
    public MasterServerCommandNew masterServerCommand;

    @Override
    public void run() {
        CommandLine commandLine = new CommandLine(this);
        commandLine.setErr(masterServerCommand.output);
        commandLine.setOut(masterServerCommand.output);
        commandLine.usage(masterServerCommand.output);
    }
}
