package ch.realmtech.server.cli;

import ch.realmtech.server.mod.options.OptionLoader;
import ch.realmtech.server.registry.RegistryUtils;

import java.util.Optional;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.ParentCommand;

@Command(name = "save", description = "save options to file")
public class OptionsSaveCommand implements Runnable {
    @ParentCommand
    OptionsCommand optionsCommand;
    @Override
    public void run() {
        Optional<OptionLoader> optionLoader = RegistryUtils.findEntry(optionsCommand.masterCommand.getRootRegistry(), OptionLoader.class);
        if (optionLoader.isPresent()) {
            optionsCommand.masterCommand.getContext().getExecuteOnContext().onServer((serverContext) -> {
                try {
                    optionLoader.get().saveServerOptions();
                    optionsCommand.masterCommand.output.println("Server option file saved.");
                } catch (Exception e) {
                    optionsCommand.masterCommand.output.println("Server option file can not be save. " + e.getMessage());
                }
            });

            optionsCommand.masterCommand.getContext().getExecuteOnContext().onClientWorld((systemsAdminClientForClient, world) -> {
                try {
                    optionLoader.get().saveClientOption();
                    optionsCommand.masterCommand.output.println("Client option file saved.");
                } catch (Exception e) {
                    optionsCommand.masterCommand.output.println("Client option file can not be save. " + e.getMessage());
                }
            });
        } else {
            optionsCommand.masterCommand.output.println("Option loader is not in the registry, strange, is normally parte of RealmTech option registry");
        }
    }
}
