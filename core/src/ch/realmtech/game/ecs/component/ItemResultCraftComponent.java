package ch.realmtech.game.ecs.component;

import ch.realmtech.game.item.ItemResultCraftPickEvent;
import ch.realmtech.game.registery.CraftingRecipeEntry;
import com.artemis.Component;

public class ItemResultCraftComponent extends Component {
    public ItemResultCraftPickEvent pickEvent;
    public CraftingRecipeEntry craftingRecipeEntry;

    public ItemResultCraftComponent set(ItemResultCraftPickEvent pickEvent, CraftingRecipeEntry craftingRecipeEntry) {
        this.pickEvent = pickEvent;
        this.craftingRecipeEntry = craftingRecipeEntry;
        return this;
    }
}
