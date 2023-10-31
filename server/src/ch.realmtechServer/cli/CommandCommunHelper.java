package ch.realmtechServer.cli;

public final class CommandCommunHelper {
    public static String echo(String[] messages) {
        return String.join(" ", messages);
    }
}
