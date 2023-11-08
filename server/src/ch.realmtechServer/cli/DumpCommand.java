package ch.realmtechServer.cli;


import picocli.CommandLine;

import java.util.concurrent.Callable;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.ParentCommand;

@Command(name = "dump",  description = "master dump command, see sub command for more dump details",
        mixinStandardHelpOptions = true,
        subcommands = {
                DumpItemsCommand.class,
                DumpChunksCommand.class,
                DumpPlayersCommand.class,
        }
)
public class DumpCommand implements Callable<Integer> {
    @ParentCommand
    CommunMasterCommand masterCommand;
    @Override
    public Integer call() throws Exception {
        CommandLine commandLine = new CommandLine(this);
        commandLine.setErr(masterCommand.output);
        commandLine.setOut(masterCommand.output);
        commandLine.execute("-h");
        return 0;
    }
}
