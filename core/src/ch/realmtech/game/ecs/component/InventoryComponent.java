package ch.realmtech.game.ecs.component;

import com.artemis.Component;
public class InventoryComponent extends Component {
    public final static int STACK_LIMITE = 64;
    public final static int NUMBER_OF_ROW = 4;
    public final static int NUMBER_OF_SLOT_PAR_ROW = 9;
    public int[][] inventory;
    public void set(int numberOfSlot) {
        inventory = new int[numberOfSlot][STACK_LIMITE];
    }
}
