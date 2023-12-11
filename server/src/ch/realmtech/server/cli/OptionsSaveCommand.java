package ch.realmtech.server.cli;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.ParentCommand;

@Command(name = "save", description = "save options to file")
public class OptionsSaveCommand implements Runnable {
    @ParentCommand
    OptionsCommand optionsCommand;
    @Override
    public void run() {
        try {
            optionsCommand.masterCommand.getOptionCtrl().save();
            optionsCommand.masterCommand.output.println("Successfully save options to file");
        } catch (Exception e) {
            optionsCommand.masterCommand.output.println("Option file can not be save. " + e.getMessage());
        }
    }
}
