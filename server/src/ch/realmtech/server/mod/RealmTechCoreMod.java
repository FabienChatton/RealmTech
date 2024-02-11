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
                    .playerWalkSound(SoundManager.FOOT_STEP_GRASS_2, 1f)
                    .breakStepNeed(CellBeingMineComponent.INFINITE_MINE)
                    .tiledTexture(1,1)
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
