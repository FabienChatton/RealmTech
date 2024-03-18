package ch.realmtech.server.cli;

import ch.realmtech.server.newRegistry.NewEntry;
import ch.realmtech.server.newRegistry.OptionClientEntry;
import ch.realmtech.server.newRegistry.OptionServerEntry;
import ch.realmtech.server.newRegistry.RegistryUtils;

import java.util.List;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.ParentCommand;

@Command(name = "reset", description = "reset all options")
public class OptionsResetCommand implements Runnable {
    @ParentCommand
    OptionsCommand optionsCommand;

    @Override
    public void run() {
        optionsCommand.masterCommand.getContext().getExecuteOnContext().onClientWorld((systemsAdminClientForClient, world) -> {
            List<? extends NewEntry> clientOptions = RegistryUtils.findEntries(optionsCommand.masterCommand.getRootRegistry(), "#clientOptions");
            clientOptions.forEach((clientOption) -> ((OptionClientEntry<?>) clientOption).resetValue());
            optionsCommand.masterCommand.output.println(String.format("(%d) options reset value", clientOptions.size()));
        });

        optionsCommand.masterCommand.getContext().getExecuteOnContext().onServer((world) -> {
            List<? extends NewEntry> serverOptions = RegistryUtils.findEntries(optionsCommand.masterCommand.getRootRegistry(), "#serverOptions");
            serverOptions.forEach((serverOption) -> ((OptionServerEntry<?>) serverOption).resetValue());
            optionsCommand.masterCommand.output.println(String.format("(%d) options reset value", serverOptions.size()));
        });
    }
}
