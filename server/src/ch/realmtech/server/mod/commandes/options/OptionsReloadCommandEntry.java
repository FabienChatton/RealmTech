package ch.realmtech.server.mod.commandes.options;

import ch.realmtech.server.mod.options.OptionLoader;
import ch.realmtech.server.registry.CommandEntry;
import ch.realmtech.server.registry.Entry;
import ch.realmtech.server.registry.OptionServerEntry;
import ch.realmtech.server.registry.RegistryUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.ParentCommand;

@Command(name = "reload", description = "Reload options from file")
public class OptionsReloadCommandEntry extends CommandEntry {
    public OptionsReloadCommandEntry() {
        super("Reload");
    }

    @ParentCommand
    public OptionsCommandEntry optionsCommand;

    @Override
    public void run() {
        Optional<OptionLoader> optionLoaderOpt = RegistryUtils.findEntry(optionsCommand.masterCommand.getRootRegistry(), OptionLoader.class);
        if (optionLoaderOpt.isEmpty()) {
            optionsCommand.masterCommand.output.println("Option loader is missing.");
            return;
        }
        OptionLoader optionLoader = optionLoaderOpt.get();
        optionsCommand.masterCommand.getContext().getExecuteOnContext().onServer((systemsAdminServer, serverContext) -> {
            try {
                optionLoader.loadServerProperties();
            } catch (IOException e) {
                optionsCommand.masterCommand.output.println("Can not load server properties file.");
                return;
            }
            List<? extends Entry> serverOptions = RegistryUtils.findEntries(optionsCommand.masterCommand.getRootRegistry(), "#serverOptions");
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
            List<? extends Entry> clientOptions = RegistryUtils.findEntries(optionsCommand.masterCommand.getRootRegistry(), "#clientOptions");
            clientOptions.forEach((clientOption) -> ((OptionServerEntry<?>) clientOption).setValueFromProperties());
            optionsCommand.masterCommand.output.println(String.format("(%d) client options reloaded", clientOptions.size()));
        });
    }
}
