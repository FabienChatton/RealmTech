package ch.realmtech.server.mod.commandes.options;

import ch.realmtech.server.registry.*;

import java.util.List;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.ParentCommand;

@Command(name = "reset", description = "reset all options to default")
public class OptionsResetCommandEntry extends CommandEntry {
    public OptionsResetCommandEntry() {
        super("OptionsReset");
    }

    @ParentCommand
    public OptionsCommandEntry optionsCommand;

    @Override
    public void run() {
        optionsCommand.masterCommand.getContext().getExecuteOnContext().onClientWorld((systemsAdminClientForClient, world) -> {
            List<? extends Entry> clientOptions = RegistryUtils.findEntries(optionsCommand.masterCommand.getRootRegistry(), "#clientOptions");
            clientOptions.forEach((clientOption) -> ((OptionClientEntry<?>) clientOption).resetValue());
            optionsCommand.masterCommand.output.println(String.format("(%d) options reset value", clientOptions.size()));
        });

        optionsCommand.masterCommand.getContext().getExecuteOnContext().onServer((systemsAdminServer, serverContext) -> {
            List<? extends Entry> serverOptions = RegistryUtils.findEntries(optionsCommand.masterCommand.getRootRegistry(), "#serverOptions");
            serverOptions.forEach((serverOption) -> ((OptionServerEntry<?>) serverOption).resetValue());
            optionsCommand.masterCommand.output.println(String.format("(%d) options reset value", serverOptions.size()));
        });
    }
}
