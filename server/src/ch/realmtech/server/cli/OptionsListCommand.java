package ch.realmtech.server.cli;


import java.util.Map;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.ParentCommand;

@Command(name = "list", description = "list name and value of all available options")
public class OptionsListCommand implements Runnable {
    @ParentCommand
    OptionsCommand optionsCommand;
    @Override
    public void run() {
        Map<String, String> listOptions = optionsCommand.masterCommand.getOptionCtrl().listOptions();
        listOptions.forEach((optionName, optionStringValue) -> optionsCommand.masterCommand.output.println(String.format("%s -> %s", optionName, optionStringValue)));
    }
}
