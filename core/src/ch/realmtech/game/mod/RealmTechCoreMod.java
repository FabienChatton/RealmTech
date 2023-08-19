package ch.realmtech.game.mod;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.component.*;
import ch.realmtech.game.ecs.system.PlayerInventorySystem;
import ch.realmtech.game.inventory.AddAndDisplayInventoryArgs;
import ch.realmtech.game.inventory.DisplayInventoryArgs;
import ch.realmtech.game.item.ItemBehavior;
import ch.realmtech.game.item.ItemType;
import ch.realmtech.game.level.cell.CellBehavior;
import ch.realmtech.game.level.cell.Cells;
import ch.realmtech.game.level.cell.CreatePhysiqueBody;
import ch.realmtech.game.registery.*;
import ch.realmtech.sound.SoundManager;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

import java.util.function.Consumer;


public class RealmTechCoreMod extends ModInitializerManager {
    @Wire
    private TextureAtlas textureAtlas;
    @Wire(name = "context")
    private RealmTech context;

    public final static String MOD_ID = "realmtech";
    public final static Registry<CellRegisterEntry> CELLS = Registry.create(MOD_ID);
    public final static Registry<ItemRegisterEntry> ITEMS = Registry.create(MOD_ID);
    public final static InfRegistryAnonyme<CraftingRecipeEntry> CRAFT = InfRegistryAnonyme.create();
    public final static InfRegistryAnonyme<CraftingRecipeEntry> FURNACE_RECIPE = InfRegistryAnonyme.create();

    @Override
    public void initialize() {
        RealmTechCoreCraftingRecipe.initCraftingRecipe(CRAFT);
        RealmTechCoreCraftingRecipe.initFurnaceRecipe(FURNACE_RECIPE);
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
                    .physiqueBody(CreatePhysiqueBody.defaultPhysiqueBody())
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
                    .interagieClickDroit((world, cellId) -> {
                        ComponentMapper<CraftingTableComponent> mCrafting = world.getMapper(CraftingTableComponent.class);
                        ComponentMapper<InventoryComponent> mInventory = world.getMapper(InventoryComponent.class);
                        CraftingTableComponent craftingTableComponent = mCrafting.get(cellId);
                        world.getSystem(PlayerInventorySystem.class).toggleInventoryWindow(context -> {
                            final Table playerInventory = new Table(context.getSkin());
                            final Table craftingInventory = new Table(context.getSkin());
                            final Table craftingResultInventory = new Table(context.getSkin());
                            Consumer<Window> addTable = window -> {
                                window.add(craftingInventory).padBottom(10f).right();
                                window.add(craftingResultInventory).padBottom(10f).row();
                                window.add(playerInventory);
                            };
                            InventoryComponent inventoryComponent = mInventory.get(context.getEcsEngine().getPlayerId());
                            InventoryComponent inventoryCraft = mInventory.get(craftingTableComponent.craftingInventory);
                            InventoryComponent inventoryResult = mInventory.get(craftingTableComponent.craftingResultInventory);
                            return new AddAndDisplayInventoryArgs(addTable, new DisplayInventoryArgs[]{
                                    DisplayInventoryArgs.builder(inventoryComponent, playerInventory).build(),
                                    DisplayInventoryArgs.builder(inventoryCraft, craftingInventory).crafting().build(),
                                    DisplayInventoryArgs.builder(inventoryResult, craftingResultInventory).notClickAndDropDst().craftResult().build()
                            });
                        });
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
                    .interagieClickDroit((world, cellId) -> {
                        ComponentMapper<InventoryComponent> mInventory = world.getMapper(InventoryComponent.class);
                        InventoryComponent inventoryComponent = mInventory.get(cellId);
                        world.getSystem(PlayerInventorySystem.class).toggleInventoryWindow(context -> {
                            final Table playerInventory = new Table(context.getSkin());
                            final Table inventory = new Table(context.getSkin());

                            Consumer<Window> addTable = window -> {
                                window.add(inventory).padBottom(10f).row();
                                window.add(playerInventory);
                            };
                            return new AddAndDisplayInventoryArgs(addTable, new DisplayInventoryArgs[]{
                                    DisplayInventoryArgs.builder(mInventory.get(context.getEcsEngine().getPlayerId()), playerInventory).build(),
                                    DisplayInventoryArgs.builder(inventoryComponent, inventory).build()
                            });
                        });
                    })
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
    public final static CellItemRegisterEntry FURNACE = registerCellItem("furnace", new CellRegisterEntry(
            "furnace-01",
            CellBehavior.builder(Cells.Layer.BUILD_DECO)
                    .editEntity((world, id) -> {
                        FurnaceComponent furnaceComponent = world.edit(id).create(FurnaceComponent.class);
                        int inventoryItemToSmelt = world.create();
                        int inventoryCarburant = world.create();
                        int inventoryResult = world.create();

                        world.edit(inventoryItemToSmelt).create(InventoryComponent.class).set(1, 1, InventoryComponent.DEFAULT_BACKGROUND_TEXTURE_NAME);
                        world.edit(inventoryItemToSmelt).create(CraftingComponent.class).set(FURNACE_RECIPE, inventoryResult);
                        world.edit(inventoryCarburant).create(InventoryComponent.class).set(1, 1, InventoryComponent.DEFAULT_BACKGROUND_TEXTURE_NAME);
                        world.edit(inventoryResult).create(InventoryComponent.class).set(1, 1, InventoryComponent.DEFAULT_BACKGROUND_TEXTURE_NAME);
                        furnaceComponent.set(inventoryItemToSmelt, inventoryCarburant, inventoryResult);
                    })
                    .interagieClickDroit((world, cellId) -> {
                        ComponentMapper<FurnaceComponent> mFurnace = world.getMapper(FurnaceComponent.class);
                        ComponentMapper<InventoryComponent> mInventory = world.getMapper(InventoryComponent.class);
                        FurnaceComponent furnaceComponent = mFurnace.get(cellId);

                        world.getSystem(PlayerInventorySystem.class).toggleInventoryWindow(context -> {
                            InventoryComponent inventoryItemToSmelt = mInventory.get(furnaceComponent.inventoryItemToSmelt);
                            InventoryComponent inventoryCarburant = mInventory.get(furnaceComponent.inventoryCarburant);
                            InventoryComponent inventoryResult = mInventory.get(furnaceComponent.inventoryResult);
                            InventoryComponent inventoryPlayer = mInventory.get(context.getEcsEngine().getPlayerId());

                            Table playerInventoryTable = new Table(context.getSkin());
                            Table itemToSmeltTable = new Table(context.getSkin());
                            Table carburantTable = new Table(context.getSkin());
                            Table resultTable = new Table(context.getSkin());
                            Consumer<Window> addTable = window -> {
                                window.add(itemToSmeltTable).row();
                                window.add(resultTable).padLeft(100f).row();
                                window.add(carburantTable).row();
                                window.add(playerInventoryTable);
                            };
                            return new AddAndDisplayInventoryArgs(addTable, new DisplayInventoryArgs[]{
                                    DisplayInventoryArgs.builder(inventoryItemToSmelt, itemToSmeltTable).crafting().build(),
                                    DisplayInventoryArgs.builder(inventoryCarburant, carburantTable).build(),
                                    DisplayInventoryArgs.builder(inventoryResult, resultTable).notClickAndDropDst().craftResult().build(),
                                    DisplayInventoryArgs.builder(inventoryPlayer, playerInventoryTable).build()
                            });
                        });
                    })
                    .breakWith(ItemType.PIOCHE, "realmtech.furnace")
                    .physiqueBody(CreatePhysiqueBody.defaultPhysiqueBody())
                    .build()
    ), new ItemRegisterEntry(
            "furnace-01",
            ItemBehavior.builder()
                    .placeCell("realmtech.furnace")
                    .build()
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
