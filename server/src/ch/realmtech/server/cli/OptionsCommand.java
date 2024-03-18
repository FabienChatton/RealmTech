package ch.realmtech.server.cli;


import picocli.CommandLine;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.ParentCommand;

@Command(name = "options", aliases = "option", description = "master commande of option for server. see sub commande for more info",
    subcommands = {
        OptionsGetCommand.class,
        OptionsListCommand.class,
        OptionsSaveCommand.class,
        OptionsReloadCommand.class,
        OptionsSetCommand.class,
            OptionsResetCommand.class,
    }
)
public class OptionsCommand implements Runnable {
    @ParentCommand
    CommunMasterCommand masterCommand;

    @Override
    public void run() {
        CommandLine commandLine = new CommandLine(this);
        commandLine.setErr(masterCommand.output);
        commandLine.setOut(masterCommand.output);
        commandLine.usage(masterCommand.output);
    }
}
