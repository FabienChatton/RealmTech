package ch.realmtechServer.cli;

import java.util.concurrent.Callable;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.ParentCommand;

@Command(name = "stop", description = "stop le serveur", mixinStandardHelpOptions = true)
public class StopCommand implements Callable<Integer> {
    @ParentCommand
    private MasterCommand masterCommand;
    @Override
    public Integer call() throws Exception {
        masterCommand.commandServerContext.closeServer().await();
        return 0;
    }
}
