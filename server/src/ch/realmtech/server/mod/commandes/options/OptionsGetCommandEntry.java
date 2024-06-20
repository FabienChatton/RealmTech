package ch.realmtech.server.mod.commandes.options;

import ch.realmtech.server.registry.CommandEntry;
import ch.realmtech.server.registry.OptionEntry;
import ch.realmtech.server.registry.RegistryUtils;

import java.util.Optional;

import static picocli.CommandLine.*;

@Command(name = "get", description = "get value of a option")
public class OptionsGetCommandEntry extends CommandEntry {
    public OptionsGetCommandEntry() {
        super("OptionsGet");
    }

    @ParentCommand
    public OptionsCommandEntry optionsCommand;

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
