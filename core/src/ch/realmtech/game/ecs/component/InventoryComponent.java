package ch.realmtech.game.ecs.component;

import com.artemis.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class InventoryComponent extends Component {
    public final static int DEFAULT_STACK_LIMITE = 64;
    public final static int DEFAULT_NUMBER_OF_ROW = 4;
    public final static int DEFAULT_NUMBER_OF_SLOT_PAR_ROW = 9;
    public int[][] inventory;
    public int stackLimite;
    public int numberOfRow;
    public int numberOfSlotParRow;
    public TextureRegion backgroundTexture;
    public void set(int numberOfSlotParRow, int numberOfRow, TextureRegion backgroundTexture) {
        this.stackLimite = DEFAULT_STACK_LIMITE;
        this.numberOfRow = numberOfRow;
        this.numberOfSlotParRow = numberOfSlotParRow;
        inventory = new int[numberOfSlotParRow * numberOfRow][DEFAULT_STACK_LIMITE];
        this.backgroundTexture = backgroundTexture;
    }
}
