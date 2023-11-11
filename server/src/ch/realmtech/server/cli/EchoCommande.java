package ch.realmtech.server.cli;

import java.util.concurrent.Callable;

import static picocli.CommandLine.*;

@Command(name = "echo", description = "Display a line of text.", mixinStandardHelpOptions = true)
public class EchoCommande implements Callable<Integer> {
    @ParentCommand
    private CommunMasterCommand masterServerCommand;
    @Parameters(description = "The String to display")
    private String[] messages;
    @Override
    public Integer call() throws Exception {
        if (messages != null) {
            masterServerCommand.output.println(String.join(" ", messages));
        }
        return 0;
    }
}
