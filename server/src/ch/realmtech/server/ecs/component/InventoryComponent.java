package ch.realmtech.server.ecs.component;

import com.artemis.Component;

public class InventoryComponent extends Component {
    public final static int DEFAULT_STACK_LIMITE = 64;
    public final static int DEFAULT_NUMBER_OF_ROW = 4;
    public final static int DEFAULT_NUMBER_OF_SLOT_PAR_ROW = 9;

    public int[][] inventory;
    public int stackLimite;
    public int numberOfRow;
    public int numberOfSlotParRow;

    public InventoryComponent set(int[][] inventory, int numberOfSlotParRow, int numberOfRow) {
        this.stackLimite = DEFAULT_STACK_LIMITE;
        this.numberOfRow = numberOfRow;
        this.numberOfSlotParRow = numberOfSlotParRow;
        this.inventory = inventory;
        return this;
    }

    public InventoryComponent set(int numberOfSlotParRow, int numberOfRow) {
        set(new int[numberOfSlotParRow * numberOfRow][DEFAULT_STACK_LIMITE], numberOfSlotParRow, numberOfRow);
        return this;
    }

    @Override
    public String toString() {
        return String.format("row: %d, column: %d", numberOfRow, numberOfSlotParRow);
    }
}
