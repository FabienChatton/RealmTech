package ch.realmtech.server.cli;


import static picocli.CommandLine.*;

@Command(name = "summon", description = "Summon a ia.")
public class SummonCommand implements Runnable {
    @ParentCommand
    MasterServerCommand masterServerCommand;

    @Parameters(index = "0")
    int x;

    @Parameters(index = "1")
    int y;

    @Override
    public void run() {
        masterServerCommand.serverContext.getSystemsAdmin().iaSystemServer.createIaTest(x, y);
    }
}
