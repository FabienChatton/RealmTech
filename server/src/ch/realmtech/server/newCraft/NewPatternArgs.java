package ch.realmtech.server.newCraft;

public class NewPatternArgs {
    private final char symbol;
    private final String itemName;

    public NewPatternArgs(char symbol, String itemName) {
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
