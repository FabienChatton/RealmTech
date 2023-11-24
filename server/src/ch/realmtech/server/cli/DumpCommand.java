package ch.realmtech.server.cli;


import picocli.CommandLine;

import java.util.concurrent.Callable;

import static picocli.CommandLine.*;

@Command(name = "dump",  description = "master dump command, see subcommands for more dump details.\n" +
                                        "Each subcommands has a verbose option",
        subcommands = {
                //DumpChunksCommand.class,
                //DumpItemsCommand.class,
                //DumpCraftResultCommand.class,
                //DumpPlayersCommand.class,
                DumpInventoryCommand.class
        }
)
public class DumpCommand implements Callable<Integer> {
    @ParentCommand
    CommunMasterCommand masterCommand;
    private int verboseLevel = 0;

    @Option(names = {"-v", "--verbose"}, description = "Show more detail about. verbose can be multiple for ever more detail", scope = ScopeType.INHERIT)
    private void setVerbose(boolean[] verbose) {
        verboseLevel = verbose.length;
    }

    public void printlnVerbose(int verboseLevel, Object x) {
        if (this.verboseLevel >= verboseLevel) {
            masterCommand.output.println(x);
        }
    }

    @Override
    public Integer call() throws Exception {
        CommandLine commandLine = new CommandLine(this);
        commandLine.setErr(masterCommand.output);
        commandLine.setOut(masterCommand.output);
        commandLine.usage(masterCommand.output);
        return 0;
    }
}
