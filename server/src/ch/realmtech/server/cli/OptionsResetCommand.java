package ch.realmtech.server.cli;

import ch.realmtech.server.registry.Entry;
import ch.realmtech.server.registry.OptionClientEntry;
import ch.realmtech.server.registry.OptionServerEntry;
import ch.realmtech.server.registry.RegistryUtils;

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
            List<? extends Entry> clientOptions = RegistryUtils.findEntries(optionsCommand.masterCommand.getRootRegistry(), "#clientOptions");
            clientOptions.forEach((clientOption) -> ((OptionClientEntry<?>) clientOption).resetValue());
            optionsCommand.masterCommand.output.println(String.format("(%d) options reset value", clientOptions.size()));
        });

        optionsCommand.masterCommand.getContext().getExecuteOnContext().onServer((world) -> {
            List<? extends Entry> serverOptions = RegistryUtils.findEntries(optionsCommand.masterCommand.getRootRegistry(), "#serverOptions");
            serverOptions.forEach((serverOption) -> ((OptionServerEntry<?>) serverOption).resetValue());
            optionsCommand.masterCommand.output.println(String.format("(%d) options reset value", serverOptions.size()));
        });
    }
}
