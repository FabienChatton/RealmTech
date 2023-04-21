package ch.realmtech.game.ecs.component;

import ch.realmtech.game.registery.CraftingRecipeEntry;
import ch.realmtech.game.registery.RegistryAnonyme;
import com.artemis.Component;
import com.badlogic.gdx.utils.Array;


public class CraftingComponent extends Component {
    public Array<CraftingRecipeEntry> craftingRecipe;

    public CraftingComponent() {
        craftingRecipe = new Array<>();
    }

    public void set(RegistryAnonyme<CraftingRecipeEntry> registry) {
        for (int i = 0; i < registry.size; i++) {
            craftingRecipe.add(registry.get(i));
        }
    }

}
