package ch.realmtech.server.mod.commandes.masterCommand;

import ch.realmtech.server.cli.CommendContext;

import java.io.PrintWriter;

import static picocli.CommandLine.Command;

@Command(name = "common", aliases = "c", description = "Commun master command")
public abstract class MasterCommonCommandNew implements Runnable, CommendContext {
    public final PrintWriter output;

    public MasterCommonCommandNew(PrintWriter output) {
        this.output = output;
    }

    @Override
    public void run() {

    }
}
