package ch.realmtech.server.cli;

import ch.realmtech.server.newMod.options.OptionLoader;
import ch.realmtech.server.newRegistry.NewEntry;
import ch.realmtech.server.newRegistry.OptionServerEntry;
import ch.realmtech.server.newRegistry.RegistryUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.ParentCommand;

@Command(name = "reload", description = "Reload options from file")
public class OptionsReloadCommand implements Runnable {
    @ParentCommand
    OptionsCommand optionsCommand;
    @Override
    public void run() {
        Optional<OptionLoader> optionLoaderOpt = RegistryUtils.findEntry(optionsCommand.masterCommand.getRootRegistry(), OptionLoader.class);
        if (optionLoaderOpt.isEmpty()) {
            optionsCommand.masterCommand.output.println("Option loader is missing.");
            return;
        }
        OptionLoader optionLoader = optionLoaderOpt.get();
        optionsCommand.masterCommand.getContext().getExecuteOnContext().onServer((serverContext) -> {
            try {
                optionLoader.loadServerProperties();
            } catch (IOException e) {
                optionsCommand.masterCommand.output.println("Can not load server properties file.");
                return;
            }
            List<? extends NewEntry> serverOptions = RegistryUtils.findEntries(optionsCommand.masterCommand.getRootRegistry(), "#serverOptions");
            serverOptions.forEach((serverOption) -> ((OptionServerEntry<?>) serverOption).setValueFromProperties());
            optionsCommand.masterCommand.output.println(String.format("(%d) server options reloaded", serverOptions.size()));
        });

        optionsCommand.masterCommand.getContext().getExecuteOnContext().onClientWorld((clientForClient, world) -> {
            try {
                optionLoader.loadClientProperties();
            } catch (IOException e) {
                optionsCommand.masterCommand.output.println("Can not load server properties file.");
                return;
            }
            List<? extends NewEntry> clientOptions = RegistryUtils.findEntries(optionsCommand.masterCommand.getRootRegistry(), "#clientOptions");
            clientOptions.forEach((clientOption) -> ((OptionServerEntry<?>) clientOption).setValueFromProperties());
            optionsCommand.masterCommand.output.println(String.format("(%d) client options reloaded", clientOptions.size()));
        });
    }
}
