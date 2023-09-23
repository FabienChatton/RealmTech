package ch.realmtechServer.netty;

import java.util.Random;
import java.util.concurrent.Callable;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.Option;

@Command(name = "connectionConfiguration", description = "La configuration pour la connection.", version = "0.1", mixinStandardHelpOptions = true)
public class ConnectionCommand implements Callable<ConnectionBuilder> {
    @Option(names = {"-p", "--port"}, description = "Le port", defaultValue = "25533")
    private int port;

    @Option(names = {"-s", "--saveName"}, description = "Quel carte sera chargée par le serveur", defaultValue = "default")
    private String saveName;

    @Override
    public ConnectionBuilder call() throws Exception {
        return new ConnectionBuilder()
                .setSaveName(saveName)
                .setPort(port);
    }
}
