package ch.realmtechServer.cli;

import java.util.concurrent.Callable;

import static picocli.CommandLine.*;

@Command(name = "echo", description = "echo un message", mixinStandardHelpOptions = true)
public class EchoCommande implements Callable<Integer> {
    @ParentCommand
    private CommunMasterCommand masterServerCommand;
    @Parameters(description = "Le message a echo")
    private String[] messages;
    @Override
    public Integer call() throws Exception {
        if (messages != null) {
            masterServerCommand.output.println(String.join(" ", messages));
        }
        return 0;
    }
}
