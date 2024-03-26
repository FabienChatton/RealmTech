package ch.realmtech.server.craft;

import ch.realmtech.server.registry.ItemEntry;

/**
 * Pattern arguments are used to complement the pattern. Each character in the pattern
 * correspond to a symbol in the pattern args. <br />
 * Exemple of a shape entry:
 * <pre>
 *  super("WoodenPickaxeCraft", "realmtech.items.WoodenPickaxe", 1, new char[][]{
 *          {'p', 'p', 'p'},
 *          {' ', 's', ' '},
 *          {' ', 's', ' '}
 *  }, new PatternArgs('p', "realmtech.items.Plank"), new PatternArgs('s', "realmtech.items.Stick"));
 * </pre>
 *
 * @param symbol   A symbol used in the pattern.
 * @param itemFqrn What is the {@link ItemEntry} related to this symbol.
 */
public record PatternArgs(char symbol, String itemFqrn) {
}
