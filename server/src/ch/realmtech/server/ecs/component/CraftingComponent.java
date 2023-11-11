package ch.realmtech.server.ecs.component;

import ch.realmtech.server.registery.CraftingRecipeEntry;
import ch.realmtech.server.registery.InfRegistryAnonyme;
import com.artemis.Component;
import com.artemis.annotations.EntityId;
import com.badlogic.gdx.utils.Array;


public class CraftingComponent extends Component {
    public Array<CraftingRecipeEntry> craftingRecipe;
    @EntityId
    public int resultInventory;

    public CraftingComponent() {
        craftingRecipe = new Array<>();
    }

    public void set(InfRegistryAnonyme<CraftingRecipeEntry> registry, int resultInventory) {
        for (int i = 0; i < registry.getEnfants().size(); i++) {
            craftingRecipe.add(registry.getEnfants().get(i).getEntry());
        }
        this.resultInventory = resultInventory;
    }

}
