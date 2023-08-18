package ch.realmtech.game.mod;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.component.CellBeingMineComponent;
import ch.realmtech.game.ecs.component.CraftingComponent;
import ch.realmtech.game.ecs.component.CraftingTableComponent;
import ch.realmtech.game.ecs.component.InventoryComponent;
import ch.realmtech.game.item.ItemBehavior;
import ch.realmtech.game.item.ItemType;
import ch.realmtech.game.level.cell.CellBehavior;
import ch.realmtech.game.level.cell.Cells;
import ch.realmtech.game.registery.*;
import ch.realmtech.sound.SoundManager;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import static ch.realmtech.game.level.cell.CreatePhysiqueBody.defaultPhysiqueBody;

public class RealmTechCoreMod extends ModInitializerManager {
    @Wire
    private TextureAtlas textureAtlas;
    @Wire(name = "context")
    private RealmTech context;

    public final static String MOD_ID = "realmtech";
    public final static Registry<CellRegisterEntry> CELLS = Registry.create(MOD_ID);
    public final static Registry<ItemRegisterEntry> ITEMS = Registry.create(MOD_ID);
    public final static InfRegistryAnonyme<CraftingRecipeEntry> CRAFT = InfRegistryAnonyme.create();

    @Override
    public void initialize() {
        RealmTechCoreCraftingRecipe.initCraftingRecipe(CRAFT);
    }

    //<editor-fold desc="registre des cellules">
    public final static CellRegisterEntry GRASS_CELL = registerCell("grass", new CellRegisterEntry(
            "grass-01",
            CellBehavior.builder(Cells.Layer.GROUND)
                    .playerWalkSound(SoundManager.FOOT_STEP_GRASS_2, 1f)
                    .breakStepNeed(CellBeingMineComponent.INFINITE_MINE)
                    .build()
    ));
    public final static CellRegisterEntry SAND_CELL = registerCell("sand", new CellRegisterEntry(
            "sand-01",
            CellBehavior.builder(Cells.Layer.GROUND)
                    .playerWalkSound(SoundManager.FOOT_STEP_SAND_1, 0.25f)
                    .breakStepNeed(CellBeingMineComponent.INFINITE_MINE)
                    .build()
    ));
    public final static CellRegisterEntry WATER_CELL = registerCell("water", new CellRegisterEntry(
            "water-01",
            CellBehavior.builder(Cells.Layer.GROUND)
                    .speedEffect(0.5f)
                    .playerWalkSound(SoundManager.FOOT_STEP_WATER_1, 0.25f)
                    .breakStepNeed(CellBeingMineComponent.INFINITE_MINE)
                    .build()
    ));
    public final static CellRegisterEntry TREE_CELL = registerCell("tree", new CellRegisterEntry(
            "tree-02",
            CellBehavior.builder(Cells.Layer.GROUND_DECO)
                    .breakWith(ItemType.TOUS, "realmtech.buche")
                    .physiqueBody(defaultPhysiqueBody())
                    .build()
    ));
    //</editor-fold>


    //<editor-fold desc="registre des items">
    public final static ItemRegisterEntry NO_ITEM = registerItem("noitem", new ItemRegisterEntry(
            "default-texture",
            ItemBehavior.builder().build()
    ));
    public final static ItemRegisterEntry PIOCHE_BOIS_ITEM = registerItem("piocheBois", new ItemRegisterEntry(
            "pioche-bois-01",
            ItemBehavior.builder()
                    .setItemType(ItemType.PIOCHE)
                    .build()
    ));
    public final static ItemRegisterEntry PIOCHE_STONE_ITEM = registerItem("piocheStone", new ItemRegisterEntry(
            "pioche-stone-01",
            ItemBehavior.builder()
                    .setItemType(ItemType.PIOCHE)
                    .build()
    ));
    public final static ItemRegisterEntry PELLE_BOIS_ITEM = registerItem("pelleBois", new ItemRegisterEntry(
            "pelle-bois-01",
            ItemBehavior.builder()
                    .setItemType(ItemType.PELLE)
                    .build()
    ));
    public final static ItemRegisterEntry PELLE_STONE_ITEM = registerItem("pelleStone", new ItemRegisterEntry(
            "pelle-stone-01",
            ItemBehavior.builder()
                    .setItemType(ItemType.PELLE)
                    .build()
    ));
    public final static ItemRegisterEntry SANDALES_ITEM = registerItem("sandales", new ItemRegisterEntry(
            "sandales-01",
            ItemBehavior.builder()
                    .setSpeedEffect(2)
                    .build()
    ));
    public final static ItemRegisterEntry BUCHE_ITEM = registerItem("buche", new ItemRegisterEntry(
            "buche-01",
            ItemBehavior.builder()
                    .build()
    ));
    public final static ItemRegisterEntry STICK_ITEM = registerItem("stick", new ItemRegisterEntry(
            "stick-02",
            ItemBehavior.builder()
                    .build()
    ));
    //</editor-fold>


    //<editor-fold desc="registre des Cell/item">
    public final static CellItemRegisterEntry PLANCHE_CELL_ITEM = registerCellItem("planche", new CellRegisterEntry(
            "plank-cell-01",
            CellBehavior.builder(Cells.Layer.BUILD)
                    .breakWith(ItemType.TOUS)
                    .dropOnBreak("realmtech.planche")
                    .build()
    ), new ItemRegisterEntry(
            "plank-02",
            ItemBehavior.builder()
                    .placeCell("realmtech.planche")
                    .build()
    ));
    public final static CellItemRegisterEntry CRAFTING_TABLE = registerCellItem("craftingTable", new CellRegisterEntry(
            "table-craft-01",
            CellBehavior.builder(Cells.Layer.BUILD_DECO)
                    .breakWith(ItemType.TOUS, "realmtech.craftingTable")
                    .editEntity((world, cellId) -> {
                        int craftingInventory = world.create();
                        int craftingResultInventory = world.create();
                        world.edit(cellId).create(CraftingTableComponent.class).set(craftingInventory, craftingResultInventory);
                        world.edit(craftingInventory).create(InventoryComponent.class).set(3, 3, InventoryComponent.DEFAULT_BACKGROUND_TEXTURE_NAME);
                        world.edit(craftingResultInventory).create(InventoryComponent.class).set(1, 1, InventoryComponent.DEFAULT_BACKGROUND_TEXTURE_NAME);
                        world.edit(craftingInventory).create(CraftingComponent.class).set(RealmTechCoreMod.CRAFT, craftingResultInventory);
                    })
                    .build()
    ), new ItemRegisterEntry(
            "table-craft-01",
            ItemBehavior.builder()
                    .placeCell("realmtech.craftingTable")
                    .build()
    ));

    public final static CellItemRegisterEntry CHEST = registerCellItem("chest", new CellRegisterEntry(
            "chest-01",
            CellBehavior.builder(Cells.Layer.BUILD_DECO)
                    .breakWith(ItemType.TOUS, "realmtech.chest")
                    .editEntity((world, cellId) -> world.edit(cellId).create(InventoryComponent.class).set(9, 3, InventoryComponent.DEFAULT_BACKGROUND_TEXTURE_NAME))
                    .build()
    ), new ItemRegisterEntry(
            "chest-01",
            ItemBehavior.builder()
                    .placeCell("realmtech.chest")
                    .build()
    ));

    public final static CellItemRegisterEntry COPPER = registerCellItem("copper", new CellRegisterEntry(
            "copper-ore-03",
            CellBehavior.builder(Cells.Layer.GROUND_DECO)
                    .breakWith(ItemType.PIOCHE, "realmtech.copper")
                    .build()
    ), new ItemRegisterEntry(
            "copper-ore-03",
            ItemBehavior.builder().build()
    ));

    public final static CellItemRegisterEntry STONE = registerCellItem("stone", new CellRegisterEntry(
            "stone-ore-01",
            CellBehavior.builder(Cells.Layer.GROUND_DECO)
                    .breakWith(ItemType.PIOCHE, "realmtech.stone")
                    .build()
    ), new ItemRegisterEntry(
            "stone-ore-01",
            ItemBehavior.builder().build()
    ));

    public final static CellItemRegisterEntry IRON = registerCellItem("iron", new CellRegisterEntry(
            "iron-ore-01",
            CellBehavior.builder(Cells.Layer.GROUND_DECO)
                    .breakWith(ItemType.PIOCHE, "realmtech.iron")
                    .build()
    ), new ItemRegisterEntry(
            "iron-ore-01",
            ItemBehavior.builder().build()
    ));

    public final static CellItemRegisterEntry COAL = registerCellItem("coal", new CellRegisterEntry(
            "coal-ore-01",
            CellBehavior.builder(Cells.Layer.GROUND_DECO)
                    .breakWith(ItemType.PIOCHE, "realmtech.coal")
                    .build()
    ), new ItemRegisterEntry(
            "coal-ore-01",
            ItemBehavior.builder().build()
    ));
    public final static CellItemRegisterEntry GOLD = registerCellItem("gold", new CellRegisterEntry(
            "gold-ore-01",
            CellBehavior.builder(Cells.Layer.GROUND_DECO)
                    .breakWith(ItemType.PIOCHE, "realmtech.gold")
                    .build()
    ), new ItemRegisterEntry(
            "gold-ore-01",
            ItemBehavior.builder().build()
    ));
    //</editor-fold>
    private static CellRegisterEntry registerCell(final String name, final CellRegisterEntry cellRegisterEntry) {
        return CELLS.add(name, cellRegisterEntry);
    }

    private static ItemRegisterEntry registerItem(final String name, final ItemRegisterEntry itemRegisterEntry) {
        return ITEMS.add(name, itemRegisterEntry);
    }

    private static CellItemRegisterEntry registerCellItem(final String name, final CellRegisterEntry cellRegisterEntry, final ItemRegisterEntry itemRegisterEntry) {
        registerItem(name, itemRegisterEntry);
        registerCell(name, cellRegisterEntry);
        return new CellItemRegisterEntry(cellRegisterEntry, itemRegisterEntry);
    }
}
