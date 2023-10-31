package ch.realmtechServer.cli;


import static picocli.CommandLine.Option;

public class ConsoleContextOption {

    @Option(names = {"-c", "--client"}, description = "execute la commande sur le client.")
    boolean client;

    @Option(names = {"-s", "--server"}, description = "execute la commande sur le serveur.")
    boolean server;

    public Context getContext() {
        if (client) {
            return Context.CLIENT;
        }
        if (server) {
            return Context.SERVER;
        }
        return null;
    }

    public enum Context {
        CLIENT, SERVER,
    }
}
