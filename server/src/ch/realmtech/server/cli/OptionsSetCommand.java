package ch.realmtech.server.cli;

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
        optionsCommand.masterCommand.getOptionCtrl().setOptionValue(optionName, optionValue);
    }
}
