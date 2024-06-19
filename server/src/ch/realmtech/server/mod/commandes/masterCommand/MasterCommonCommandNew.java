package ch.realmtech.server.mod.commandes.masterCommand;

import java.io.PrintWriter;

import static picocli.CommandLine.Command;

@Command(name = "common", aliases = "c", description = "Commun master command")
public abstract class MasterCommonCommandNew implements Runnable {
    public final PrintWriter output;

    public MasterCommonCommandNew(PrintWriter output) {
        this.output = output;
    }

    @Override
    public void run() {

    }
}
