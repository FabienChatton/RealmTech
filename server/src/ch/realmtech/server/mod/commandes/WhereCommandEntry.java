package ch.realmtech.server.mod.commandes;

import ch.realmtech.server.datactrl.DataCtrl;
import ch.realmtech.server.mod.commandes.masterCommand.MasterCommonCommandNew;
import ch.realmtech.server.registry.CommandEntry;

import java.nio.file.Path;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.ParentCommand;

@Command(name = "where", description = "where root path is located")
public class WhereCommandEntry extends CommandEntry {
    public WhereCommandEntry() {
        super("Where");
    }

    @ParentCommand
    public MasterCommonCommandNew communMasterCommand;

    @Override
    public void run() {
        communMasterCommand.output.println(Path.of(DataCtrl.getRootPath()).normalize().toAbsolutePath());
    }
}
