package ch.realmtechServer.netty;

import java.util.concurrent.Callable;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.Option;

@Command(name = "connectionConfiguration", description = "La configuration pour la connection.")
public class ConnectionCommand implements Callable<ConnectionBuilder> {
    @Option(names = {"-p", "--port"}, description = "Le port")
    private int port;

    @Override
    public ConnectionBuilder call() throws Exception {
        return new ConnectionBuilder().setPort(port);
    }
}
