package ch.realmtech.server.mod.commandes.dump;

import ch.realmtech.server.mod.commandes.masterCommand.MasterCommonCommandNew;
import ch.realmtech.server.registry.CommandEntry;
import picocli.CommandLine;

import java.util.Optional;
import java.util.function.Supplier;

import static picocli.CommandLine.*;

@Command(name = "dump",  description = "master dump command, see subcommands for more dump details.\n" +
        "Each subcommands has a verbose option", mixinStandardHelpOptions = true)
public class DumpCommandEntry extends CommandEntry {

    public DumpCommandEntry() {
        super("Dump");
    }

    @ParentCommand
    public MasterCommonCommandNew masterCommand;

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

    public void atVerboseLevel(int verboseLevel, Runnable runAt) {
        if (this.verboseLevel >= verboseLevel) {
            runAt.run();
        }
    }

    public <T> Optional<T> atVerboseLevel(int verboseLevel, Supplier<T> runAt) {
        if (this.verboseLevel >= verboseLevel) {
            return Optional.of(runAt.get());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void run() {
        CommandLine commandLine = new CommandLine(this);
        commandLine.setErr(masterCommand.output);
        commandLine.setOut(masterCommand.output);
        commandLine.usage(masterCommand.output);
    }
}
