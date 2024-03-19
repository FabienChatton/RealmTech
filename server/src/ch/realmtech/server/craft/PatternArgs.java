package ch.realmtech.server.craft;

public class PatternArgs {
    private final char symbol;
    private final String itemName;

    public PatternArgs(char symbol, String itemName) {
        this.symbol = symbol;
        this.itemName = itemName;
    }

    public char getSymbol() {
        return symbol;
    }

    public String getItemName() {
        return itemName;
    }
}
