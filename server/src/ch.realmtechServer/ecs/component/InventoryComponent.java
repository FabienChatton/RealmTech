package ch.realmtechServer.ecs.component;

import com.artemis.Component;
import com.badlogic.gdx.utils.Null;

import java.util.UUID;

public class InventoryComponent extends Component {
    public final static int DEFAULT_STACK_LIMITE = 64;
    public final static int DEFAULT_NUMBER_OF_ROW = 4;
    public final static int DEFAULT_NUMBER_OF_SLOT_PAR_ROW = 9;
    public final static String DEFAULT_BACKGROUND_TEXTURE_NAME = "inventory-02";
    public final static String NO_BACKGROUND_TEXTURE_NAME = "no-texture";

    public int[][] inventory;
    public int stackLimite;
    public int numberOfRow;
    public int numberOfSlotParRow;
    public String backgroundTexture;
    // ajouter un jour dans le set. il ne devrait pas etre null
    @Null
    public UUID uuid;

    public InventoryComponent set(int numberOfSlotParRow, int numberOfRow, String backgroundTextureName) {
        this.stackLimite = DEFAULT_STACK_LIMITE;
        this.numberOfRow = numberOfRow;
        this.numberOfSlotParRow = numberOfSlotParRow;
        inventory = new int[numberOfSlotParRow * numberOfRow][DEFAULT_STACK_LIMITE];
        this.backgroundTexture = backgroundTextureName;
        return this;
    }
}
