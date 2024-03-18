package ch.realmtech.server.cli;


import ch.realmtech.server.datactrl.DataCtrl;

import java.nio.file.Path;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.ParentCommand;

@Command(name = "where", description = "where root path is located")
public class WhereCommand implements Runnable {
    @ParentCommand
    CommunMasterCommand communMasterCommand;

    @Override
    public void run() {
        communMasterCommand.output.println(Path.of(DataCtrl.getRootPath()).normalize().toAbsolutePath());
    }
}
