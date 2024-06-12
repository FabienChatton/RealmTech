package ch.realmtech.server.cli;


import ch.realmtech.server.registry.OptionEntry;
import ch.realmtech.server.registry.RegistryUtils;

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
        Optional<OptionEntry<?>> optionEntry = RegistryUtils.findEntry(optionsCommand.masterCommand.getRootRegistry(), optionName);
        if (optionEntry.isPresent()) {
            optionsCommand.masterCommand.output.println(optionEntry.get().getValue());
        } else {
            optionsCommand.masterCommand.output.println("No option found for \"" + optionName + "\".");
        }
    }
}
