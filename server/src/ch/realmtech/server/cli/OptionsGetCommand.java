package ch.realmtech.server.cli;


import java.util.Optional;

import static picocli.CommandLine.*;

@Command(name = "get", description = "get value of a option")
public class OptionsGetCommand implements Runnable {
    @ParentCommand
    OptionsCommand optionsCommand;
    @Parameters(description = "The option name to display the value")
    private String optionName;

    @Override
    public void run() {
        Optional<?> option = optionsCommand.masterCommand.getOptionCtrl().getOptionValue(optionName);
        if (option.isPresent()) {
            optionsCommand.masterCommand.output.println(option.get());
        } else {
            optionsCommand.masterCommand.output.println("No option found for \"" + optionName + "\".");
        }
    }
}
