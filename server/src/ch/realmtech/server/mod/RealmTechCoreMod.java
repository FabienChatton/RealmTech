package ch.realmtech.server.mod;


import ch.realmtech.server.ecs.ExecuteOnContext;
import ch.realmtech.server.ecs.component.*;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import ch.realmtech.server.ecs.system.InventoryManager;
import ch.realmtech.server.ecs.system.MapManager;
import ch.realmtech.server.ecs.system.UuidComponentManager;
import ch.realmtech.server.energy.EnergyBatteryEditEntity;
import ch.realmtech.server.energy.EnergyCableEditEntity;
import ch.realmtech.server.energy.EnergyGeneratorEditEntity;
import ch.realmtech.server.inventory.AddAndDisplayInventoryArgs;
import ch.realmtech.server.inventory.DisplayInventoryArgs;
import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.item.ItemType;
import ch.realmtech.server.level.WrenchRightClick;
import ch.realmtech.server.level.cell.*;
import ch.realmtech.server.packet.serverPacket.InventoryGetPacket;
import ch.realmtech.server.registery.*;
import ch.realmtech.server.sound.SoundManager;
import com.artemis.ArtemisPlugin;
import com.artemis.ComponentMapper;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

import java.util.UUID;
import java.util.function.Consumer;


public class RealmTechCoreMod implements ArtemisPlugin {

    public final static String MOD_ID = "realmtech";
    public final static Registry<CellRegisterEntry> CELLS = Registry.create(MOD_ID);
    public final static Registry<ItemRegisterEntry> ITEMS = Registry.create(MOD_ID);
    public final static Registry<QuestEntry> QUESTS = Registry.create(MOD_ID);
    public final static InfRegistryAnonyme<CraftingRecipeEntry> CRAFT = InfRegistryAnonyme.create();
    public final static InfRegistryAnonyme<CraftingRecipeEntry> FURNACE_RECIPE = InfRegistryAnonyme.create();

    @Override
    public void setup(WorldConfigurationBuilder b) {
        RealmTechCoreCraftingRecipe.initCraftingRecipe(CRAFT);
        RealmTechCoreCraftingRecipe.initFurnaceRecipe(FURNACE_RECIPE);
    }

    //<editor-fold desc="registre des items">
    public final static ItemRegisterEntry NO_ITEM = registerItem("noitem", new ItemRegisterEntry(
            "default-texture",
            ItemBehavior.builder().setItemType(ItemType.HAND).build()
    ));
    public final static ItemRegisterEntry PIOCHE_BOIS_ITEM = registerItem("piocheBois", new ItemRegisterEntry(
            "pioche-bois-01",
            ItemBehavior.builder()
                    .setItemType(ItemType.PICKAXE)
                    .build()
    ));
    public final static ItemRegisterEntry PIOCHE_STONE_ITEM = registerItem("piocheStone", new ItemRegisterEntry(
            "pioche-stone-01",
            ItemBehavior.builder()
                    .setItemType(ItemType.PICKAXE)
                    .build()
    ));
    public final static ItemRegisterEntry PELLE_BOIS_ITEM = registerItem("pelleBois", new ItemRegisterEntry(
            "pelle-bois-01",
            ItemBehavior.builder()
                    .setItemType(ItemType.SHOVEL)
                    .build()
    ));
    public final static ItemRegisterEntry PELLE_STONE_ITEM = registerItem("pelleStone", new ItemRegisterEntry(
            "pelle-stone-01",
            ItemBehavior.builder()
                    .setItemType(ItemType.SHOVEL)
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
                    .setTimeToBurn(60)
                    .build()
    ));
    public final static ItemRegisterEntry STICK_ITEM = registerItem("stick", new ItemRegisterEntry(
            "stick-02",
            ItemBehavior.builder()
                    .build()
    ));
    public final static ItemRegisterEntry TIN_INGOT = registerItem("tinIngot", new ItemRegisterEntry(
            "tin-ingot-01",
            ItemBehavior.builder().build()
    ));
    public final static ItemRegisterEntry COAL = registerItem("coal", new ItemRegisterEntry(
            "coal-01",
            ItemBehavior.builder().setTimeToBurn(1000).build()
    ));
    public final static ItemRegisterEntry GOLD_INGOT = registerItem("goldIngot", new ItemRegisterEntry(
            "gold-ingot-01",
            ItemBehavior.builder().build()
    ));
    public final static ItemRegisterEntry IRON_INGOT = registerItem("ironIngot", new ItemRegisterEntry(
            "iron-ingot-01",
            ItemBehavior.builder().build()
    ));
    public final static ItemRegisterEntry COPPER_INGOT = registerItem("copperIngot", new ItemRegisterEntry(
            "copper-ingot-01",
            ItemBehavior.builder().build()
    ));
    public final static ItemRegisterEntry WRENCH = registerItem("wrench", new ItemRegisterEntry(
            "pelle-stone-01",
            ItemBehavior.builder()
                    .interagieClickDroit(WrenchRightClick.wrenchRightClick())
                    .build()
    ));

    public final static ItemRegisterEntry ICON_FURNACE_TIME_TO_BURN_01 = registerItem("iconFurnaceBurn", new ItemRegisterEntry("furnace-time-to-burn-01", ItemBehavior.builder().icon().build()));
    public final static ItemRegisterEntry ICON_FURNACE_TIME_TO_BURN_02 = registerItem("iconFurnaceBurn", new ItemRegisterEntry("furnace-time-to-burn-02", ItemBehavior.builder().icon().build()));
    public final static ItemRegisterEntry ICON_FURNACE_TIME_TO_BURN_03 = registerItem("iconFurnaceBurn", new ItemRegisterEntry("furnace-time-to-burn-03", ItemBehavior.builder().icon().build()));
    public final static ItemRegisterEntry ICON_FURNACE_TIME_TO_BURN_04 = registerItem("iconFurnaceBurn", new ItemRegisterEntry("furnace-time-to-burn-04", ItemBehavior.builder().icon().build()));
    public final static ItemRegisterEntry ICON_FURNACE_TIME_TO_BURN_05 = registerItem("iconFurnaceBurn", new ItemRegisterEntry("furnace-time-to-burn-05", ItemBehavior.builder().icon().build()));
    public final static ItemRegisterEntry ICON_FURNACE_TIME_TO_BURN_06 = registerItem("iconFurnaceBurn", new ItemRegisterEntry("furnace-time-to-burn-06", ItemBehavior.builder().icon().build()));
    public final static ItemRegisterEntry ICON_FURNACE_TIME_TO_BURN_07 = registerItem("iconFurnaceBurn", new ItemRegisterEntry("furnace-time-to-burn-07", ItemBehavior.builder().icon().build()));
    public final static ItemRegisterEntry ICON_FURNACE_TIME_TO_BURN_08 = registerItem("iconFurnaceBurn", new ItemRegisterEntry("furnace-time-to-burn-08", ItemBehavior.builder().icon().build()));
    public final static ItemRegisterEntry ICON_FURNACE_TIME_TO_BURN_09 = registerItem("iconFurnaceBurn", new ItemRegisterEntry("furnace-time-to-burn-09", ItemBehavior.builder().icon().build()));
    public final static ItemRegisterEntry ICON_FURNACE_TIME_TO_BURN_10 = registerItem("iconFurnaceBurn", new ItemRegisterEntry("furnace-time-to-burn-10", ItemBehavior.builder().icon().build()));
    public final static ItemRegisterEntry ICON_FURNACE_ARROW_01 = registerItem("iconFurnaceArrow", new ItemRegisterEntry("furnace-arrow-01", ItemBehavior.builder().icon().build()));
    public final static ItemRegisterEntry ICON_FURNACE_ARROW_02 = registerItem("iconFurnaceArrow", new ItemRegisterEntry("furnace-arrow-02", ItemBehavior.builder().icon().build()));
    public final static ItemRegisterEntry ICON_FURNACE_ARROW_03 = registerItem("iconFurnaceArrow", new ItemRegisterEntry("furnace-arrow-03", ItemBehavior.builder().icon().build()));
    public final static ItemRegisterEntry ICON_FURNACE_ARROW_04 = registerItem("iconFurnaceArrow", new ItemRegisterEntry("furnace-arrow-04", ItemBehavior.builder().icon().build()));
    public final static ItemRegisterEntry ICON_FURNACE_ARROW_05 = registerItem("iconFurnaceArrow", new ItemRegisterEntry("furnace-arrow-05", ItemBehavior.builder().icon().build()));
    public final static ItemRegisterEntry ICON_FURNACE_ARROW_06 = registerItem("iconFurnaceArrow", new ItemRegisterEntry("furnace-arrow-06", ItemBehavior.builder().icon().build()));
    public final static ItemRegisterEntry ICON_FURNACE_ARROW_07 = registerItem("iconFurnaceArrow", new ItemRegisterEntry("furnace-arrow-07", ItemBehavior.builder().icon().build()));
    public final static ItemRegisterEntry ICON_FURNACE_ARROW_08 = registerItem("iconFurnaceArrow", new ItemRegisterEntry("furnace-arrow-08", ItemBehavior.builder().icon().build()));
    public final static ItemRegisterEntry ICON_FURNACE_ARROW_09 = registerItem("iconFurnaceArrow", new ItemRegisterEntry("furnace-arrow-09", ItemBehavior.builder().icon().build()));
    public final static ItemRegisterEntry ICON_FURNACE_ARROW_10 = registerItem("iconFurnaceArrow", new ItemRegisterEntry("furnace-arrow-10", ItemBehavior.builder().icon().build()));
    //</editor-fold>

    //<editor-fold desc="registre des cellules">
    public final static CellRegisterEntry GRASS_CELL = registerCell("grass", new CellRegisterEntry(
            "grass-01",
            CellBehavior.builder(Cells.Layer.GROUND)
                    .playerWalkSound(1f, SoundManager.FOOT_STEP_GRASS_1, SoundManager.FOOT_STEP_GRASS_2, SoundManager.FOOT_STEP_GRASS_3, SoundManager.FOOT_STEP_GRASS_4, SoundManager.FOOT_STEP_GRASS_5, SoundManager.FOOT_STEP_GRASS_6, SoundManager.FOOT_STEP_GRASS_7, SoundManager.FOOT_STEP_GRASS_8, SoundManager.FOOT_STEP_GRASS_9)
                    .breakStepNeed(CellBeingMineComponent.INFINITE_MINE)
                    .tiledTexture(1,1)
                    .build()
    ));
    public final static CellRegisterEntry SAND_CELL = registerCell("sand", new CellRegisterEntry(
            "sand-01",
            CellBehavior.builder(Cells.Layer.GROUND)
                    .playerWalkSound(0.25f, SoundManager.FOOT_STEP_SAND_1, SoundManager.FOOT_STEP_SAND_2, SoundManager.FOOT_STEP_SAND_3, SoundManager.FOOT_STEP_SAND_4, SoundManager.FOOT_STEP_SAND_5, SoundManager.FOOT_STEP_SAND_6, SoundManager.FOOT_STEP_SAND_7, SoundManager.FOOT_STEP_SAND_8, SoundManager.FOOT_STEP_SAND_9, SoundManager.FOOT_STEP_SAND_10)
                    .breakStepNeed(CellBeingMineComponent.INFINITE_MINE)
                    .tiledTexture(2, 2)
                    .build()
    ));
    public final static CellRegisterEntry WATER_CELL = registerCell("water", new CellRegisterEntry(
            "water-01",
            CellBehavior.builder(Cells.Layer.GROUND)
                    .speedEffect(0.5f)
                    .playerWalkSound(0.25f, SoundManager.FOOT_STEP_WATER_1, SoundManager.FOOT_STEP_WATER_2, SoundManager.FOOT_STEP_WATER_3, SoundManager.FOOT_STEP_WATER_4)
                    .breakStepNeed(CellBeingMineComponent.INFINITE_MINE)
                    .build()
    ));
    public final static CellRegisterEntry TREE_CELL = registerCell("tree", new CellRegisterEntry(
            "tree-02",
            CellBehavior.builder(Cells.Layer.GROUND_DECO)
                    .breakWith(ItemType.HAND, "realmtech.buche")
                    .physiqueBody(CreatePhysiqueBody.defaultPhysiqueBody())
                    .build()
    ));
    //</editor-fold>


    //<editor-fold desc="registre des Cell/item">
    public final static CellItemRegisterEntry PLANCHE_CELL_ITEM = registerCellItem("planche", new CellRegisterEntry(
            "plank-cell-01",
            CellBehavior.builder(Cells.Layer.BUILD)
                    .breakWith(ItemType.HAND)
                    .playerWalkSound(1f, SoundManager.FOOT_STEP_WOOD_1, SoundManager.FOOT_STEP_WOOD_2, SoundManager.FOOT_STEP_WOOD_3, SoundManager.FOOT_STEP_WOOD_4, SoundManager.FOOT_STEP_WOOD_5, SoundManager.FOOT_STEP_WOOD_6, SoundManager.FOOT_STEP_WOOD_7, SoundManager.FOOT_STEP_WOOD_8, SoundManager.FOOT_STEP_WOOD_9)
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
                    .breakWith(ItemType.HAND, "realmtech.craftingTable")
                    .physiqueBody(CreatePhysiqueBody.defaultPhysiqueBody())
                    .editEntity(CraftingTableEditEntity.createCraftingTable(3, 3))
                    .interagieClickDroit((clientContext, cellId) -> {
                        ComponentMapper<CraftingTableComponent> mCrafting = clientContext.getWorld().getMapper(CraftingTableComponent.class);
                        ComponentMapper<InventoryComponent> mInventory = clientContext.getWorld().getMapper(InventoryComponent.class);
                        CraftingTableComponent craftingTableComponent = mCrafting.get(cellId);
                        clientContext.openPlayerInventory(() -> {
                            final Table playerInventory = new Table(clientContext.getSkin());
                            final Table craftingInventory = new Table(clientContext.getSkin());
                            final Table craftingResultInventory = new Table(clientContext.getSkin());
                            Consumer<Window> addTable = (window) -> {
                                Table craftingTable = new Table(craftingInventory.getSkin());
                                craftingTable.add(craftingInventory).padRight(32f);
                                craftingTable.add(craftingResultInventory);
                                craftingTable.padBottom(10f);
                                window.add(craftingTable).row();
                                window.add(playerInventory);
                            };
                            int inventoryPlayerId = clientContext.getWorld().getSystem(InventoryManager.class).getChestInventoryId(clientContext.getPlayerId());
                            int inventoryCraftId = craftingTableComponent.craftingInventory;
                            int inventoryResultId = craftingTableComponent.craftingResultInventory;
                            UUID inventoryPlayerUuid = clientContext.getWorld().getSystem(UuidComponentManager.class).getRegisteredComponent(inventoryPlayerId).getUuid();
                            UUID inventoryCraftUuid = clientContext.getWorld().getSystem(UuidComponentManager.class).getRegisteredComponent(inventoryCraftId).getUuid();
                            UUID inventoryResultUuid = clientContext.getWorld().getSystem(UuidComponentManager.class).getRegisteredComponent(inventoryResultId).getUuid();

                            clientContext.sendRequest(new InventoryGetPacket(inventoryPlayerUuid));
                            clientContext.sendRequest(new InventoryGetPacket(inventoryCraftUuid));
                            clientContext.sendRequest(new InventoryGetPacket(inventoryResultUuid));
                            SystemsAdminCommun systemsAdminCommun = clientContext.getWorld().getRegistered("systemsAdmin");

                            return new AddAndDisplayInventoryArgs(addTable, new DisplayInventoryArgs[] {
                                    DisplayInventoryArgs.builder(systemsAdminCommun.uuidComponentManager.getRegisteredComponent(inventoryPlayerId).getUuid(), playerInventory).build(),
                                    DisplayInventoryArgs.builder(systemsAdminCommun.uuidComponentManager.getRegisteredComponent(inventoryCraftId).getUuid(), craftingInventory).build(),
                                    DisplayInventoryArgs.builder(systemsAdminCommun.uuidComponentManager.getRegisteredComponent(inventoryResultId).getUuid(), craftingResultInventory).notClickAndDropDst().build()
                            }, new UUID[] {inventoryCraftUuid, inventoryResultUuid});
                        });
                    })
                    .build()
    ), new ItemRegisterEntry(
            "table-craft-01",
            ItemBehavior.builder()
                    .placeCell("realmtech.craftingTable")
                    .build()
    ));

    public final static CellItemRegisterEntry FURNACE = registerCellItem("furnace", new CellRegisterEntry(
            "furnace-01",
            CellBehavior.builder(Cells.Layer.BUILD_DECO)
                    .breakWith(ItemType.HAND, "realmtech.furnace")
                    .physiqueBody(CreatePhysiqueBody.defaultPhysiqueBody())
                    .editEntity(FurnaceEditEntity.createFurnace())
                    .interagieClickDroit((clientContext, cellId) -> {
                        ComponentMapper<CraftingTableComponent> mCrafting = clientContext.getWorld().getMapper(CraftingTableComponent.class);
                        ComponentMapper<InventoryComponent> mInventory = clientContext.getWorld().getMapper(InventoryComponent.class);
                        ComponentMapper<FurnaceComponent> mFurnace = clientContext.getWorld().getMapper(FurnaceComponent.class);
                        ComponentMapper<FurnaceIconsComponent> mFurnaceIcons = clientContext.getWorld().getMapper(FurnaceIconsComponent.class);

                        CraftingTableComponent craftingTableComponent = mCrafting.get(cellId);
                        FurnaceComponent furnaceComponent = mFurnace.get(cellId);
                        FurnaceIconsComponent furnaceIconsComponent = mFurnaceIcons.get(cellId);
                        clientContext.openPlayerInventory(() -> {
                            Table playerInventory = new Table(clientContext.getSkin());
                            Table craftingInventory = new Table(clientContext.getSkin());
                            Table craftingResultInventory = new Table(clientContext.getSkin());
                            Table carburantInventory = new Table(clientContext.getSkin());
                            Table iconFire = new Table(clientContext.getSkin());
                            Table iconProcess= new Table(clientContext.getSkin());

                            Consumer<Window> addTable = (window) -> {
                                Table craftingTable = new Table(craftingInventory.getSkin());
                                craftingTable.add(craftingInventory);
                                craftingTable.row();

                                craftingTable.add(iconFire);
                                craftingTable.add(iconProcess).padLeft(32f);
                                craftingTable.add(craftingResultInventory).padLeft(32f);
                                craftingTable.row();

                                craftingTable.add(carburantInventory);
                                craftingTable.row();

                                craftingTable.padBottom(10f);
                                window.add(craftingTable).row();
                                window.add(playerInventory);
                            };
                            int inventoryPlayerId = clientContext.getWorld().getSystem(InventoryManager.class).getChestInventoryId(clientContext.getPlayerId());
                            int inventoryCraftId = craftingTableComponent.craftingInventory;
                            int inventoryResultId = craftingTableComponent.craftingResultInventory;
                            int inventoryCarburantId = furnaceComponent.inventoryCarburant;
                            int iconFireId = furnaceIconsComponent.getIconFire();
                            int iconProcessId = furnaceIconsComponent.getIconProcess();
                            UUID inventoryPlayerUuid = clientContext.getWorld().getSystem(UuidComponentManager.class).getRegisteredComponent(inventoryPlayerId).getUuid();
                            UUID inventoryCraftUuid = clientContext.getWorld().getSystem(UuidComponentManager.class).getRegisteredComponent(inventoryCraftId).getUuid();
                            UUID inventoryResultUuid = clientContext.getWorld().getSystem(UuidComponentManager.class).getRegisteredComponent(inventoryResultId).getUuid();
                            UUID inventoryCarburantUuid = clientContext.getWorld().getSystem(UuidComponentManager.class).getRegisteredComponent(inventoryCarburantId).getUuid();

                            clientContext.sendRequest(new InventoryGetPacket(inventoryPlayerUuid));
                            clientContext.sendRequest(new InventoryGetPacket(inventoryCraftUuid));
                            clientContext.sendRequest(new InventoryGetPacket(inventoryResultUuid));
                            clientContext.sendRequest(new InventoryGetPacket(inventoryCarburantUuid));
                            SystemsAdminCommun systemsAdminCommun = clientContext.getWorld().getRegistered("systemsAdmin");

                            return new AddAndDisplayInventoryArgs(addTable, new DisplayInventoryArgs[] {
                                    DisplayInventoryArgs.builder(systemsAdminCommun.uuidComponentManager.getRegisteredComponent(inventoryPlayerId).getUuid(), playerInventory).build(),
                                    DisplayInventoryArgs.builder(systemsAdminCommun.uuidComponentManager.getRegisteredComponent(inventoryCraftId).getUuid(), craftingInventory).dstRequire(FurnaceEditEntity.testValideItemForCraft()).build(),
                                    DisplayInventoryArgs.builder(systemsAdminCommun.uuidComponentManager.getRegisteredComponent(inventoryResultId).getUuid(), craftingResultInventory).notClickAndDropDst().build(),
                                    DisplayInventoryArgs.builder(systemsAdminCommun.uuidComponentManager.getRegisteredComponent(inventoryCarburantId).getUuid(), carburantInventory).dstRequire(FurnaceEditEntity.testValideItemCarburant()).build(),
                                    // icons
                                    DisplayInventoryArgs.builder(systemsAdminCommun.uuidComponentManager.getRegisteredComponent(iconFireId).getUuid(), iconFire).icon().build(),
                                    DisplayInventoryArgs.builder(systemsAdminCommun.uuidComponentManager.getRegisteredComponent(iconProcessId).getUuid(), iconProcess).icon().build()
                            }, new UUID[] {inventoryCraftUuid, inventoryResultUuid, inventoryCarburantUuid});
                        });
                    })
                    .build()
    ), new ItemRegisterEntry(
            "furnace-01",
            ItemBehavior.builder()
                    .placeCell("realmtech.furnace")
                    .build()
    ));
    public final static CellItemRegisterEntry CHEST = registerCellItem("chest", new CellRegisterEntry(
            "chest-01",
            CellBehavior.builder(Cells.Layer.BUILD_DECO)
                    .breakWith(ItemType.HAND, "realmtech.chest")
                    .editEntity(ChestEditEntity.createNewInventory(9, 3))
                    .physiqueBody(CreatePhysiqueBody.defaultPhysiqueBody())
                    .interagieClickDroit((clientContext, cellId) -> {
                        ComponentMapper<ChestComponent> mChest = clientContext.getWorld().getMapper(ChestComponent.class);
                        ChestComponent chestComponent = mChest.get(cellId);
                        clientContext.openPlayerInventory(() -> {
                            final Table playerInventory = new Table(clientContext.getSkin());
                            final Table inventory = new Table(clientContext.getSkin());

                            Consumer<Window> addTable = window -> {
                                window.add(inventory).padBottom(10f).row();
                                window.add(playerInventory);
                            };

                            int inventoryPlayerId = clientContext.getWorld().getSystem(InventoryManager.class).getChestInventoryId(clientContext.getPlayerId());
                            int inventoryChestId = chestComponent.getInventoryId();

                            UUID inventoryPlayerUuid = clientContext.getWorld().getSystem(UuidComponentManager.class).getRegisteredComponent(inventoryPlayerId).getUuid();
                            UUID inventoryChestUuid = clientContext.getWorld().getSystem(UuidComponentManager.class).getRegisteredComponent(inventoryChestId).getUuid();

                            clientContext.sendRequest(new InventoryGetPacket(inventoryPlayerUuid));
                            clientContext.sendRequest(new InventoryGetPacket(inventoryChestUuid));

                            SystemsAdminCommun systemsAdminCommun = clientContext.getWorld().getRegistered("systemsAdmin");

                            return new AddAndDisplayInventoryArgs(addTable, new DisplayInventoryArgs[]{
                                    DisplayInventoryArgs.builder(systemsAdminCommun.uuidComponentManager.getRegisteredComponent(inventoryPlayerId).getUuid(), playerInventory).build(),
                                    DisplayInventoryArgs.builder(systemsAdminCommun.uuidComponentManager.getRegisteredComponent(inventoryChestId).getUuid(), inventory).build()
                            }, new UUID[] {inventoryChestUuid});
                        });
                    })
                    .build()
    ), new ItemRegisterEntry(
            "chest-01",
            ItemBehavior.builder()
                    .placeCell("realmtech.chest")
                    .build()
    ));

    public final static CellItemRegisterEntry COPPER_ORE = registerCellItem("copperOre", new CellRegisterEntry(
            "copper-ore-03",
            CellBehavior.builder(Cells.Layer.GROUND_DECO)
                    .breakWith(ItemType.PICKAXE, "realmtech.copperOre")
                    .build()
    ), new ItemRegisterEntry(
            "copper-ore-03",
            ItemBehavior.builder().build()
    ));

    public final static CellItemRegisterEntry STONE = registerCellItem("stone", new CellRegisterEntry(
            "stone-ore-01",
            CellBehavior.builder(Cells.Layer.GROUND_DECO)
                    .breakWith(ItemType.PICKAXE, "realmtech.stone")
                    .build()
    ), new ItemRegisterEntry(
            "stone-ore-01",
            ItemBehavior.builder().build()
    ));

    public final static CellItemRegisterEntry IRON_ORE = registerCellItem("ironOre", new CellRegisterEntry(
            "iron-ore-01",
            CellBehavior.builder(Cells.Layer.GROUND_DECO)
                    .breakWith(ItemType.PICKAXE, "realmtech.ironOre")
                    .build()
    ), new ItemRegisterEntry(
            "iron-ore-01",
            ItemBehavior.builder().build()
    ));
    public final static CellItemRegisterEntry TIN_ORE = registerCellItem("tinOre", new CellRegisterEntry(
            "tin-ore-01",
            CellBehavior.builder(Cells.Layer.GROUND_DECO)
                    .breakWith(ItemType.PICKAXE, "realmtech.tinOre")
                    .build()
    ), new ItemRegisterEntry(
            "tin-ore-01",
            ItemBehavior.builder().build()
    ));
    public final static CellItemRegisterEntry COAL_ORE = registerCellItem("coalOre", new CellRegisterEntry(
            "coal-ore-01",
            CellBehavior.builder(Cells.Layer.GROUND_DECO)
                    .breakWith(ItemType.PICKAXE, "realmtech.coal")
                    .build()
    ), new ItemRegisterEntry(
            "coal-ore-01",
            ItemBehavior.builder().build()
    ));
    public final static CellItemRegisterEntry GOLD_ORE = registerCellItem("goldOre", new CellRegisterEntry(
            "gold-ore-01",
            CellBehavior.builder(Cells.Layer.GROUND_DECO)
                    .breakWith(ItemType.PICKAXE, "realmtech.goldOre")
                    .build()
    ), new ItemRegisterEntry(
            "gold-ore-01",
            ItemBehavior.builder().build()
    ));

    public static CellItemRegisterEntry TORCH = registerCellItem("torch", new CellRegisterEntry("torch-01", CellBehavior
                    .builder(Cells.Layer.BUILD_DECO)
                    .editEntity(new EditEntity() {
                        @Override
                        public void createEntity(ExecuteOnContext executeOnContext, int entityId) {
                            executeOnContext.onClient((systemsAdminClient, world) -> {
                                CellComponent cellComponent = world.getMapper(CellComponent.class).get(entityId);
                                InfChunkComponent chunkComponent = world.getMapper(InfChunkComponent.class).get(cellComponent.chunkId);
                                int worldX = MapManager.getWorldPos(chunkComponent.chunkPosX, cellComponent.getInnerPosX());
                                int worldY = MapManager.getWorldPos(chunkComponent.chunkPosY, cellComponent.getInnerPosY());
                                systemsAdminClient.getLightManager().createLight(entityId, Color.valueOf("ef540b"), 15, worldX, worldY);
                            });
                        }

                        @Override
                        public void deleteEntity(ExecuteOnContext executeOnContext, int entityId) {
                            executeOnContext.onClient((systemsAdminClient, world) -> systemsAdminClient.getLightManager().disposeLight(entityId));
                        }

                        @Override
                        public void replaceEntity(ExecuteOnContext executeOnContext, int entityId) {
                            deleteEntity(executeOnContext, entityId);
                        }
                    })
                    .breakWith(ItemType.HAND, "realmtech.torch")
                    .build()),
            new ItemRegisterEntry("torch-01", ItemBehavior.builder()
                    .placeCell("realmtech.torch")
                    .build()));

    public static CellItemRegisterEntry ENERGY_BATTERY = registerCellItem("energyBattery", new CellRegisterEntry("energy-battery-01-0100", CellBehavior
                    .builder(Cells.Layer.BUILD_DECO)
                    .breakWith(ItemType.HAND, "realmtech.energyBattery")
                    .physiqueBody(CreatePhysiqueBody.defaultPhysiqueBody())
                    .editEntity(EnergyBatteryEditEntity.createDefault())
                    .interagieClickDroit((clientContext, cellId) -> {
                        EnergyBatteryComponent energyBatteryComponent = clientContext.getWorld().getMapper(EnergyBatteryComponent.class).get(cellId);
                        System.out.println(energyBatteryComponent.getStored());
                    })
                    .build()),
            new ItemRegisterEntry("energy-battery-01-0100", ItemBehavior
                    .builder()
                    .placeCell("realmtech.energyBattery")
                    .build()));

    public static CellItemRegisterEntry ENERGY_CABLE = registerCellItem("energyCable", new CellRegisterEntry("energy-cable-01-item", CellBehavior
            .builder(Cells.Layer.BUILD_DECO)
            .breakWith(ItemType.HAND, "realmtech.energyCable")
                    .editEntity(new EnergyCableEditEntity((byte) 0))
            .build()),
        new ItemRegisterEntry("energy-cable-01-item", ItemBehavior
            .builder()
            .placeCell("realmtech.energyCable")
            .build()));

    public static CellItemRegisterEntry ENERGY_GENERATOR = registerCellItem("energyGenerator", new CellRegisterEntry("furnace-01", CellBehavior
                    .builder(Cells.Layer.BUILD_DECO)
                    .breakWith(ItemType.HAND, "realmtech.energyGenerator")
                    .editEntity(EnergyGeneratorEditEntity.createDefault(), ChestEditEntity.createNewInventory(1, 1))
                    .physiqueBody(CreatePhysiqueBody.defaultPhysiqueBody())
                    .interagieClickDroit((clientContext, cellId) -> {
                        ComponentMapper<InventoryComponent> mInventory = clientContext.getWorld().getMapper(InventoryComponent.class);
                        ComponentMapper<ChestComponent> mChest = clientContext.getWorld().getMapper(ChestComponent.class);
                        ComponentMapper<EnergyGeneratorIconComponent> mEnergyBatteryIcon = clientContext.getWorld().getMapper(EnergyGeneratorIconComponent.class);

                        int carburantInventoryId = mChest.get(cellId).getInventoryId();
                        InventoryComponent carburantInventory = mInventory.get(carburantInventoryId);
                        EnergyGeneratorIconComponent energyGeneratorIconComponent = mEnergyBatteryIcon.get(cellId);
                        clientContext.openPlayerInventory(() -> {
                            Table playerInventory = new Table(clientContext.getSkin());
                            Table energyGeneratorInventory = new Table(clientContext.getSkin());
                            Table iconFire = new Table(clientContext.getSkin());

                            Consumer<Window> addTable = window -> {
                                window.add(iconFire).padBottom(2f).row();
                                window.add(energyGeneratorInventory).padBottom(10f).row();
                                window.add(playerInventory);
                            };

                            int inventoryPlayerId = clientContext.getWorld().getSystem(InventoryManager.class).getChestInventoryId(clientContext.getPlayerId());
                            int fireIconId = energyGeneratorIconComponent.getIconFireId();

                            UUID inventoryPlayerUuid = clientContext.getWorld().getSystem(UuidComponentManager.class).getRegisteredComponent(inventoryPlayerId).getUuid();
                            UUID inventoryChestUuid = clientContext.getWorld().getSystem(UuidComponentManager.class).getRegisteredComponent(carburantInventoryId).getUuid();

                            clientContext.sendRequest(new InventoryGetPacket(inventoryPlayerUuid));
                            clientContext.sendRequest(new InventoryGetPacket(inventoryChestUuid));
                            SystemsAdminCommun systemsAdminCommun = clientContext.getWorld().getRegistered("systemsAdmin");

                            return new AddAndDisplayInventoryArgs(addTable, new DisplayInventoryArgs[]{
                                    DisplayInventoryArgs.builder(systemsAdminCommun.uuidComponentManager.getRegisteredComponent(inventoryPlayerId).getUuid(), playerInventory).build(),
                                    DisplayInventoryArgs.builder(systemsAdminCommun.uuidComponentManager.getRegisteredComponent(carburantInventoryId).getUuid(), energyGeneratorInventory).build(),
                                    DisplayInventoryArgs.builder(systemsAdminCommun.uuidComponentManager.getRegisteredComponent(fireIconId).getUuid(), iconFire).icon().build()
                            }, new UUID[]{inventoryChestUuid});
                        });
                    })
                    .build()),
            new ItemRegisterEntry("furnace-01", ItemBehavior
                    .builder()
                    .placeCell("realmtech.energyGenerator")
                    .build()));

    //</editor-fold>

    //<editor-fold desc="quests">
    public static QuestEntry THE_BEGINNING = QUESTS.add("testQuest", new QuestEntry("The beginning", """
            Welcome to {WAIT} {COLOR=RED}{SHAKE}RealmTech{ENDSHAKE}{ENDCOLOR}.
            Like you, RealmTech is at the beginning of a {RAINBOW}long journey{ENDRAINBOW}.
            Your goal in this version is to {COLOR=RED}{SLOW}fill a battery with energy{ENDCOLOR}.
            This requires many small steps. This quest book will help you get there.
            """));

    public static QuestEntry KNOW_YOUR_WORLD = QUESTS.add("knowYourWorld", new QuestEntry("Know Your World", """
            RealmTech is a game that procedurally generates the Terran. You can explore endlessly.
            If you can't find a certain resource near your location, explore further.
            RealmTech is also a multiplayer game, so a friend can join your world using the multiplayer menu.
            There's a day/night cycle in the game, so you can use torches to light your way.
            These are crafted with a stick and a coal on top.
            For the moment, there are no monsters, so your life is not in danger.
            """));

    public static QuestEntry FIRST_RESOURCE = QUESTS.add("firstResources", new QuestEntry("First resources", """
            As in {COLOR=YELLOW}GregTech{ENDCOLOR}, your first goal is to {COLOR=RED}harvest wood{ENDCOLOR}.
            Hold down the left mouse button to break a tree cell to get some wood.
            To find out if you can harvest a resource, check if the text break with is green.
            """));

    public static QuestEntry FIRST_CRAFT = QUESTS.add("firstCraft", new QuestEntry("First craft", """
            Now that you've got your first resources, it's time to use them to make new items.
            Open your inventory with e. You'll find our inventory with a crafting table.
            Left-click on your logs and put them in the crafting table to make planks.
            Planks can be placed on the floor.
            """));

    public static QuestEntry CRAFTING_EXPANSION = QUESTS.add("firstCraft", new QuestEntry("First craft", """
            The first utility item you'll craft is a crafting table.
            The crafting table extends the size of the crafting table in the player's inventory.
            With the crafting table, the size of the crafting inventory is 3x3.
            With this enlarged size, more complex items and machines are available to craft.
            To make a crafting table, open the inventory and put 2x2 planks of wood in the crafting table in the player's inventory.
            After crafting the table, close the inventory and right-click to place it on the floor.
            Finally, interact with the craft table by right-clicking on the cell.
            """));

    public static QuestEntry FIRST_TOOL = QUESTS.add("firstTool", new QuestEntry("First Tool", """
            With your new crafting table, you can use it to craft your first tools. Let's build a pickaxe.
            You'll need 2 wooden sticks and 3 wooden planks.
            To obtain the sticks, you'll need to align 2 wooden planks vertically in a crafting table.
            Now, the recipe for the pickaxe is to align 3 planks of wood horizontally at the top of the craft inventory.
            Put one stick in the middle of the inventory, and another stick down the middle.
            Now you can mine stones with your pickaxe.
            """));

    public static QuestEntry THERMAL_EXPANSION = QUESTS.add("thermalExpansion", new QuestEntry("Thermal Expansion", """
            Now it's time to process your ores. To do this, you need a furnace.
            To get your first furnace, you need 8 stones, and craft the furnace in a crafting table by placing
            the 8 stones in the edges of the crafting inventory to make an "O" shape.
            To use the furnace, right-click on the furnace cell. You'll see a slot at the top for the item to be melted.
            At the bottom is the fuel slot and the slot on the right is the recipe result.
            Try putting a copper ore in the top slot and a coal in the bottom slot.
            You'll see the arrow filling up, indicating that the firing process has gone smoothly.
            Once the arrow is full, the result will be a copper ingot.
            The best fuel at this stage of the game is coal, but you can also use wood logs.
            """));

    public static QuestEntry GET_READY_FOR_ELECTRICITY = QUESTS.add("getReadyForElectricity", new QuestEntry("Get ready for electricity", """
            Introducing electricity into your world will introduce a lot of complexity.
            The first complexity will be the orientation of the cells.
            To manipulate this orientation, use a wrench.
            To chisel the wrench, place a wrench in the top corner and a wrench in the middle and bottom corners to make a Y shape.
            The notary cells that can be turned are the cables and batteries.
            To turn the cell, the location of your click is important: click on the right-hand part of the cell to turn it to the right.
            Click on the left-hand side of the cell to turn it to the left. And the same for top and bottom.
            """));

    public static QuestEntry FIRST_ENERGY_CABLE = QUESTS.add("firstEnergyCable", new QuestEntry("First Energy Cable", """
            Now that you have the ability to smelt ores, you can use the ingots to create
            the components needed for electrical installations.
            First, make copper electric cables with 3 copper ingots aligned horizontally.
            You can lay these cables on the floor,
            where they will be useful for connecting your electrical machines to your energy production.
            To connect the cables to each other and to the machines,
            right-click with a wrench on the side to which you wish to connect the cable.
            """));

    public static QuestEntry FIRST_ENERGY_BATTERY = QUESTS.add("firstEnergyBattery", new QuestEntry("First Energy Battery", """
            To create our first energy generator, we must first create a battery.
            The energy battery allows energy to be stored.
            The direction of the battery is important. To change the direction, use a wrench.
            The energy output is the direction you selected. All other directions are energy inputs.
            To recharge the battery, connect a cable connected to a power input port connected to a power source.
            As energy sources, there is an energy generator or a battery with its output side connected.
            """));

    public static QuestEntry FIRST_ENERGY_GENERATOR = QUESTS.add("firstEnergyGenerator", new QuestEntry("First Energy Generator", """
            Just having energy cables doesn't help. They take their usefulness to transport energy.
            But to transport energy, you have to have energy in the first place.
            That's what this quest is all about. Your first source of energy will be an energy generator.
            To craft the energy generator, there are two possibilities: either place the furnace form in
            the crafting inventory and place an energy battery in the center, or place a furnace with an
            energy battery in the crafting inventory (regardless of item location).
            To create your first energy units, place coal in the furnace and the furnace's internal battery will fill up.
            """));

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
