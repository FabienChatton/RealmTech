package ch.realmtech.server.cli;

import java.io.IOException;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.ParentCommand;

@Command(name = "reload", description = "Reload options from file")
public class OptionsReloadCommand implements Runnable {
    @ParentCommand
    OptionsCommand optionsCommand;
    @Override
    public void run() {
        try {
            optionsCommand.masterCommand.reloadOption();
            optionsCommand.masterCommand.output.println("Successfully reload option from file.");
        } catch (IOException e) {
            optionsCommand.masterCommand.output.println("Can not reload option from file. " + e.getMessage());
        }
    }
}
