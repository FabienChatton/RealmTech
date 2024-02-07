package ch.realmtech.server.netty;

import ch.realmtech.server.level.worldGeneration.SeedGenerator;

import java.util.concurrent.Callable;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.Option;

@Command(name = "connexionConfiguration", description = "Server launcher configuration ", version = "0.1", mixinStandardHelpOptions = true)
public class ConnexionCommand implements Callable<ConnexionConfig> {
    @Option(names = {"-p", "--port"}, description = "The port to open.", defaultValue = "25533")
    private int port;

    @Option(names = {"-s", "--saveName"}, description = "The name of the save to load. Use a none existence save name to create a new save", defaultValue = "default")
    private String saveName;

    @Option(names = {"--seed"}, description = "The seed of the save. Only apply to newly created save.")
    private long seed;

    @Override
    public ConnexionConfig call() throws Exception {
        return ConnexionConfig.builder()
                .setSaveName(saveName)
                .setPort(port)
                .setSeed(seed != 0 ? seed : SeedGenerator.randomSeed())
                .build();
    }
}
