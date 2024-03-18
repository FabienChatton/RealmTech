package ch.realmtech.server.cli;

import ch.realmtech.server.newRegistry.InvalideEvaluate;
import ch.realmtech.server.newRegistry.OptionClientEntry;
import ch.realmtech.server.newRegistry.OptionServerEntry;
import ch.realmtech.server.newRegistry.RegistryUtils;

import static picocli.CommandLine.*;

@Command(name = "set", description = "Set a value to a option")
public class OptionsSetCommand implements Runnable {
    @ParentCommand
    OptionsCommand optionsCommand;
    @Parameters(index = "0")
    String optionName;

    @Parameters(index = "1")
    String optionValue;
    @Override
    public void run() {
        optionsCommand.masterCommand.getContext().getExecuteOnContext().onClientWorld((systemsAdminClientForClient, world) -> {
            try {
                OptionClientEntry<?> optionClient = RegistryUtils.evaluateSafe(optionsCommand.masterCommand.getRootRegistry(), optionName, OptionClientEntry.class);
                optionClient.setValue(optionValue);
            } catch (InvalideEvaluate e) {
                optionsCommand.masterCommand.output.println(String.format("Can not evaluate " + optionName + ", Error: " + e.getMessage()));
            }
        });

        optionsCommand.masterCommand.getContext().getExecuteOnContext().onServer((world) -> {
            try {
                OptionServerEntry<?> optionServer = RegistryUtils.evaluateSafe(optionsCommand.masterCommand.getRootRegistry(), optionName, OptionServerEntry.class);
                optionServer.setValue(optionValue);
            } catch (InvalideEvaluate e) {
                optionsCommand.masterCommand.output.println(String.format("Can not evaluate " + optionName + ", Error: " + e.getMessage()));
            }
        });
    }
}
