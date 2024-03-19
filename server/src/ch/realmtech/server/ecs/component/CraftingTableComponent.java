package ch.realmtech.server.ecs.component;

import ch.realmtech.server.craft.CraftChange;
import ch.realmtech.server.craft.CraftResult;
import ch.realmtech.server.registry.CraftRecipeEntry;
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
    private CraftChange isCraftResultChange;
    private BiFunction<World, Integer, Function<CraftingTableComponent, Consumer<Optional<CraftResult>>>> onCraftResultChange;

    private List<CraftRecipeEntry> craftRecipes;

    public void set(int craftingInventory, int craftingResultInventory, List<CraftRecipeEntry> craftRecipes, CraftChange canProcessCraftCraftingTable, BiFunction<World, Integer, Function<CraftingTableComponent, Consumer<Optional<CraftResult>>>> onCraftResultChange) {
        this.craftingInventory = craftingInventory;
        this.craftingResultInventory = craftingResultInventory;
        this.craftRecipes = craftRecipes;
        this.isCraftResultChange = canProcessCraftCraftingTable;
        this.onCraftResultChange = onCraftResultChange;
    }

    public CraftChange getIsCraftResultChange() {
        return isCraftResultChange;
    }

    public List<CraftRecipeEntry> getCraftRecipes() {
        return craftRecipes;
    }

    public Consumer<Optional<CraftResult>> getOnCraftResultChange(World world, int craftingTableId) {
        return onCraftResultChange.apply(world, craftingTableId).apply(this);
    }
}
