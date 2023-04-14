package ch.realmtech.game.ecs.component;

import com.artemis.Component;
import com.artemis.utils.IntBag;
public class InventoryComponent extends Component {
    public int maxSize;
    public IntBag inventory;

    public void set(int maxSize) {
        this.maxSize = maxSize;
        inventory = new IntBag(maxSize);
    }
}
