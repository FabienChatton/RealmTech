package ch.realmtech.server.netty;

import java.util.concurrent.Callable;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.Option;

@Command(name = "connexionConfiguration", description = "La configuration pour la connection.", version = "0.1", mixinStandardHelpOptions = true)
public class ConnexionCommand implements Callable<ConnexionBuilder> {
    @Option(names = {"-p", "--port"}, description = "Le port", defaultValue = "25533")
    private int port;

    @Option(names = {"-s", "--saveName"}, description = "Quel carte sera chargée par le serveur", defaultValue = "default")
    private String saveName;

    @Override
    public ConnexionBuilder call() throws Exception {
        return new ConnexionBuilder()
                .setSaveName(saveName)
                .setPort(port);
    }
}
