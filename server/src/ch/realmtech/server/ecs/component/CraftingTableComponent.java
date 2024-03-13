package ch.realmtech.server.ecs.component;

import ch.realmtech.server.newCraft.NewCraftChange;
import ch.realmtech.server.newCraft.NewCraftResult;
import ch.realmtech.server.newRegistry.NewCraftRecipeEntry;
import com.artemis.Component;
import com.artemis.World;
import com.artemis.annotations.EntityId;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class CraftingTableComponent extends Component {
    @EntityId
    public int craftingInventory;

    @EntityId
    public int craftingResultInventory;
    private NewCraftChange isCraftResultChange;
    private BiFunction<World, Integer, Function<CraftingTableComponent, Consumer<Optional<NewCraftResult>>>> onCraftResultChange;

    private List<NewCraftRecipeEntry> craftRecipes;

    public void set(int craftingInventory, int craftingResultInventory, List<NewCraftRecipeEntry> craftRecipes, NewCraftChange canProcessCraftCraftingTable, BiFunction<World, Integer, Function<CraftingTableComponent, Consumer<Optional<NewCraftResult>>>> onCraftResultChange) {
        this.craftingInventory = craftingInventory;
        this.craftingResultInventory = craftingResultInventory;
        this.craftRecipes = craftRecipes;
        this.isCraftResultChange = canProcessCraftCraftingTable;
        this.onCraftResultChange = onCraftResultChange;
    }

    public NewCraftChange getIsCraftResultChange() {
        return isCraftResultChange;
    }

    public List<NewCraftRecipeEntry> getCraftRecipes() {
        return craftRecipes;
    }

    public Consumer<Optional<NewCraftResult>> getOnCraftResultChange(World world, int craftingTableId) {
        return onCraftResultChange.apply(world, craftingTableId).apply(this);
    }
}
