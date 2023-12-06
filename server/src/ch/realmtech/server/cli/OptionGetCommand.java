package ch.realmtech.server.cli;


import static picocli.CommandLine.*;

@Command(name = "get", description = "get value of a option")
public class OptionGetCommand implements Runnable {
    @ParentCommand
    private CommunMasterCommand masterCommunCommand;

    @Parameters(description = "The option name to display the value")
    private String optionKey;

    @Override
    public void run() {

    }
}
