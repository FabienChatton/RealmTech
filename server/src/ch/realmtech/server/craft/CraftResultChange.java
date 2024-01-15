package ch.realmtech.server.craft;

import java.util.Optional;

/**
 * Contains a new craft result available.
 * @param craftResult The new craft result to put the result in.
 */
public record CraftResultChange(Optional<CraftResult> craftResult) {
}
