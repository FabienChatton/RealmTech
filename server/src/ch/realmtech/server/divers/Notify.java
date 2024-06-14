package ch.realmtech.server.divers;

import java.util.Objects;

public record Notify(String title, String message, int secondeToShow) {

    public Notify {
        Objects.requireNonNull(title);
        Objects.requireNonNull(message);
    }
}
