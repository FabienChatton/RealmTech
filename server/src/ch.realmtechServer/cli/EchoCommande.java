package ch.realmtechServer.cli;

import java.util.concurrent.Callable;

import static picocli.CommandLine.*;

@Command(name = "echo", description = "echo un message")
public class EchoCommande implements Callable<Integer> {
    @ParentCommand
    private MasterCommand masterCommand;

    @Parameters(description = "Le message a echo")
    private String[] messages;
    @Override
    public Integer call() throws Exception {
        masterCommand.output.println(String.join(" ", messages));
        return 0;
    }
}
