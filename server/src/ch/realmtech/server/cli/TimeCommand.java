package ch.realmtech.server.cli;


import picocli.CommandLine;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.ParentCommand;

@Command(name = "time", description = "Master command for time manipulation, see sub command for more details.",
    subcommands = {
        TimeSetCommand.class,
        TimeGetCommand.class,
    }
)
public class TimeCommand implements Runnable {
    @ParentCommand
    MasterServerCommand masterServerCommand;
    @Override
    public void run() {
        CommandLine commandLine = new CommandLine(this);
        commandLine.setErr(masterServerCommand.output);
        commandLine.setOut(masterServerCommand.output);
        commandLine.usage(masterServerCommand.output);
    }
}
