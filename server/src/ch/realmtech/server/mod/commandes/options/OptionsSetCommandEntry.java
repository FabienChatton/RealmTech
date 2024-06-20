package ch.realmtech.server.mod.commandes.options;

import ch.realmtech.server.registry.*;

import static picocli.CommandLine.*;

@Command(name = "set", description = "Set a value to a option")
public class OptionsSetCommandEntry extends CommandEntry {
    public OptionsSetCommandEntry() {
        super("OptionsSet");
    }

    @ParentCommand
    public OptionsCommandEntry optionsCommand;

    @Parameters(index = "0")
    private String optionName;
    @Parameters(index = "1")
    private String optionValue;

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

        optionsCommand.masterCommand.getContext().getExecuteOnContext().onServer((systemsAdminServer, serverContext) -> {
            try {
                OptionServerEntry<?> optionServer = RegistryUtils.evaluateSafe(optionsCommand.masterCommand.getRootRegistry(), optionName, OptionServerEntry.class);
                optionServer.setValue(optionValue);
            } catch (InvalideEvaluate e) {
                optionsCommand.masterCommand.output.println(String.format("Can not evaluate " + optionName + ", Error: " + e.getMessage()));
            }
        });
    }
}
