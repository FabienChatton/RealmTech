package ch.realmtech.server.netty;

import ch.realmtech.server.level.worldGeneration.SeedGenerator;

import java.util.concurrent.Callable;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.Option;

@Command(name = "connexionConfiguration", description = "Server launcher configuration ", version = "0.1", mixinStandardHelpOptions = true)
public class ConnexionCommand implements Callable<ConnexionConfig> {
    @Option(names = {"-p", "--port"}, description = "The port to open.", defaultValue = "25533")
    private int port;

    @Option(names = {"-s", "--saveName"}, description = "The name of the save to load. Use a none existence save name to create a new save.", defaultValue = "default")
    private String saveName;

    @Option(names = {"--seed"}, description = "The seed of the save. Only apply to newly created save.")
    private long seed;

    @Option(names = {"-nv", "--not-verify-access-token"}, description = "Tell to not verify access token when a player join the game.")
    private boolean notVerifyAccessToken;

    @Option(names = {"-r", "--root-path"}, description = "The root path of the RealmTechData folder. Default in current directory", defaultValue = "")
    private String rootPath;

    @Override
    public ConnexionConfig call() throws Exception {
        return ConnexionConfig.builder()
                .setSaveName(saveName)
                .setPort(port, true)
                .setSeed(seed != 0 ? seed : SeedGenerator.randomSeed())
                .setVerifyAccessToken(!notVerifyAccessToken)
                .setRootPath(rootPath.isBlank() ? "" : rootPath + "/")
                .build();
    }
}
