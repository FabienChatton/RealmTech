package ch.realmtech.server.cli;


import ch.realmtech.server.registry.Entry;
import ch.realmtech.server.registry.OptionEntry;
import ch.realmtech.server.registry.RegistryUtils;

import java.util.ArrayList;
import java.util.List;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.ParentCommand;

@Command(name = "list", description = "list name and value of all available options")
public class OptionsListCommand implements Runnable {
    @ParentCommand
    OptionsCommand optionsCommand;
    @Override
    @SuppressWarnings("rawtypes")
    public void run() {
        List<OptionEntry> optionEntries = new ArrayList<>();
        optionsCommand.masterCommand.getContext().getExecuteOnContext().onClientWorld((systemsAdminClientForClient, world) -> {
            List<? extends Entry> clientOptions = RegistryUtils.findEntries(optionsCommand.masterCommand.getRootRegistry(), "#clientOptions");
            clientOptions.forEach((clientOption) -> optionEntries.add((OptionEntry) clientOption));
        });
        optionsCommand.masterCommand.getContext().getExecuteOnContext().onServer((world) -> {
            List<? extends Entry> serverOptions = RegistryUtils.findEntries(optionsCommand.masterCommand.getRootRegistry(), "#serverOptions");
            serverOptions.forEach((serverOption) -> optionEntries.add((OptionEntry) serverOption));
        });

        optionEntries.forEach((optionEntry) -> optionsCommand.masterCommand.output.println(String.format("%s -> %s", optionEntry.getName(), optionEntry.getValue())));
    }
}
