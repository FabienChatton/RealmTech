package ch.realmtech.server.craft;

import ch.realmtech.server.newCraft.NewCraftResult;

import java.util.Optional;

/**
 * Contains a new craft result available.
 * @param craftResult The new craft result to put the result in.
 */
public record CraftResultChange(Optional<NewCraftResult> craftResult) {
}
