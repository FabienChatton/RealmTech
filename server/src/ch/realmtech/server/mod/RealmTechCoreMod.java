package ch.realmtech.server.mod;

import ch.realmtech.server.ecs.Context;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.mod.cells.*;
import ch.realmtech.server.mod.commandes.*;
import ch.realmtech.server.mod.commandes.dump.*;
import ch.realmtech.server.mod.commandes.masterCommand.MasterServerCommandSupplierEntry;
import ch.realmtech.server.mod.commandes.options.*;
import ch.realmtech.server.mod.commandes.time.TimeCommandEntry;
import ch.realmtech.server.mod.commandes.time.TimeGetCommandEntry;
import ch.realmtech.server.mod.commandes.time.TimeSetCommandEntry;
import ch.realmtech.server.mod.crafts.craftingtable.*;
import ch.realmtech.server.mod.crafts.furnace.*;
import ch.realmtech.server.mod.factory.EditEntityFactory;
import ch.realmtech.server.mod.icons.*;
import ch.realmtech.server.mod.items.*;
import ch.realmtech.server.mod.mobs.ChickenMobEntry;
import ch.realmtech.server.mod.mobs.ZombieMobEntry;
import ch.realmtech.server.mod.options.OptionLoader;
import ch.realmtech.server.mod.options.client.*;
import ch.realmtech.server.mod.options.server.AuthServerBaseUrlServerOptionEntry;
import ch.realmtech.server.mod.options.server.RenderDistanceOptionEntry;
import ch.realmtech.server.mod.options.server.VerifyAccessTokenUrnOptionEntry;
import ch.realmtech.server.mod.options.server.VerifyTokenOptionEntry;
import ch.realmtech.server.mod.options.server.mob.*;
import ch.realmtech.server.mod.packets.PacketLoader;
import ch.realmtech.server.mod.questCategory.Tier0QuestCategory;
import ch.realmtech.server.mod.quests.*;
import ch.realmtech.server.registry.*;

public class RealmTechCoreMod implements ModInitializer {
    @Override
    public String getModId() {
        return "realmtech";
    }

    @Override
    public void initializeModRegistry(Registry<?> modRegistry, Context context) {
        // systems admin
        Registry<Entry> systemsAdminsRegistry = Registry.createRegistry(modRegistry, "systemsAdmin", "systems");
        Entry defaultSystemAdminClientEntry = context.getDefaultSystemAdminClientEntry();
        if (defaultSystemAdminClientEntry != null) {
            systemsAdminsRegistry.addEntry(defaultSystemAdminClientEntry);
        }
        systemsAdminsRegistry.addEntry(new SystemsAdminServer());
        // with packets
        systemsAdminsRegistry.addEntry(new PacketLoader());

        // cells
        Registry<CellEntry> cellsRegistry = Registry.createRegistry(modRegistry, "cells");
        cellsRegistry.addEntry(new ChestCellEntry());
        cellsRegistry.addEntry(new CoalOreCellEntry());
        cellsRegistry.addEntry(new CopperOreCellEntry());
        cellsRegistry.addEntry(new CraftingTableCellEntry());
        cellsRegistry.addEntry(new EnergyBatteryCellEntry());
        cellsRegistry.addEntry(new EnergyCableCellEntry());
        cellsRegistry.addEntry(new EnergyGeneratorCellEntry());
        cellsRegistry.addEntry(new FurnaceCellEntry());
        cellsRegistry.addEntry(new GoldOreCellEntry());
        cellsRegistry.addEntry(new GrassCellEntry());
        cellsRegistry.addEntry(new IronOreCellEntry());
        cellsRegistry.addEntry(new PlankCellEntry());
        cellsRegistry.addEntry(new SandCellEntry());
        cellsRegistry.addEntry(new StoneOreCellEntry());
        cellsRegistry.addEntry(new TinOreCellEntry());
        cellsRegistry.addEntry(new TorchCellEntry());
        cellsRegistry.addEntry(new TreeCellEntry());
        cellsRegistry.addEntry(new WaterCellEntry());

        // items
        Registry<ItemEntry> itemsRegistry = Registry.createRegistry(modRegistry, "items", "items");
        itemsRegistry.addEntry(new ChestItemEntry());
        itemsRegistry.addEntry(new CoalItemEntry());
        itemsRegistry.addEntry(new CoalOreItemEntry());
        itemsRegistry.addEntry(new CopperIngotItemEntry());
        itemsRegistry.addEntry(new CopperOreItemEntry());
        itemsRegistry.addEntry(new CraftingTableItemEntry());
        itemsRegistry.addEntry(new EnergyBatteryItemEntry());
        itemsRegistry.addEntry(new EnergyCableItemEntry());
        itemsRegistry.addEntry(new EnergyGeneratorItemEntry());
        itemsRegistry.addEntry(new FurnaceItemEntry());
        itemsRegistry.addEntry(new GoldIngotItemEntry());
        itemsRegistry.addEntry(new GoldOreItemEntry());
        itemsRegistry.addEntry(new IronIngotItemEntry());
        itemsRegistry.addEntry(new IronOreItemEntry());
        itemsRegistry.addEntry(new NoItemEntry());
        itemsRegistry.addEntry(new PlankItemEntry());
        itemsRegistry.addEntry(new SandalesItemEntry());
        itemsRegistry.addEntry(new StickItemEntry());
        itemsRegistry.addEntry(new StoneOreItemEntry());
        itemsRegistry.addEntry(new StonePickaxeItemEntry());
        itemsRegistry.addEntry(new StoneShovelItemEntry());
        itemsRegistry.addEntry(new TinIngotItemEntry());
        itemsRegistry.addEntry(new TinOreItemEntry());
        itemsRegistry.addEntry(new TorchItemEntry());
        itemsRegistry.addEntry(new WoodenPickaxeItemEntry());
        itemsRegistry.addEntry(new WoodenShovelItemEntry());
        itemsRegistry.addEntry(new WoodItemEntry());
        itemsRegistry.addEntry(new WrenchItemEntry());
        itemsRegistry.addEntry(new WeaponItemEntry());
        itemsRegistry.addEntry(new WoodenSwordItemEntry());
        itemsRegistry.addEntry(new StoneSwordItemEntry());
        itemsRegistry.addEntry(new RawChickenItemEntry());
        itemsRegistry.addEntry(new ChickenCookedItem());

        // icons
        Registry<Entry> iconsRegistry = Registry.createRegistry(modRegistry, "icons", "icons");
        iconsRegistry.addEntry(new ArrowIcon01Entry());
        iconsRegistry.addEntry(new ArrowIcon02Entry());
        iconsRegistry.addEntry(new ArrowIcon03Entry());
        iconsRegistry.addEntry(new ArrowIcon04Entry());
        iconsRegistry.addEntry(new ArrowIcon05Entry());
        iconsRegistry.addEntry(new ArrowIcon06Entry());
        iconsRegistry.addEntry(new ArrowIcon07Entry());
        iconsRegistry.addEntry(new ArrowIcon08Entry());
        iconsRegistry.addEntry(new ArrowIcon09Entry());
        iconsRegistry.addEntry(new ArrowIcon10Entry());

        iconsRegistry.addEntry(new FurnaceBurnIcon01Entry());
        iconsRegistry.addEntry(new FurnaceBurnIcon02Entry());
        iconsRegistry.addEntry(new FurnaceBurnIcon03Entry());
        iconsRegistry.addEntry(new FurnaceBurnIcon04Entry());
        iconsRegistry.addEntry(new FurnaceBurnIcon05Entry());
        iconsRegistry.addEntry(new FurnaceBurnIcon06Entry());
        iconsRegistry.addEntry(new FurnaceBurnIcon07Entry());
        iconsRegistry.addEntry(new FurnaceBurnIcon08Entry());
        iconsRegistry.addEntry(new FurnaceBurnIcon09Entry());
        iconsRegistry.addEntry(new FurnaceBurnIcon10Entry());

        // craft recipe
        Registry<?> crafts = Registry.createRegistry(modRegistry, "crafts");

        // craft crafting table
        Registry<CraftRecipeEntry> craftCraftingTableRegistry = Registry.createRegistry(crafts, "craftingTable", "craftingTableRecipes");
        craftCraftingTableRegistry.addEntry(new PlankCraftEntry());
        craftCraftingTableRegistry.addEntry(new StickCraftEntry());
        craftCraftingTableRegistry.addEntry(new CraftingTableCraftEntry());
        craftCraftingTableRegistry.addEntry(new ChestCraftEntry());
        craftCraftingTableRegistry.addEntry(new WoodenPickaxeCraftEntry());
        craftCraftingTableRegistry.addEntry(new WoodenShovelCraftEntry());
        craftCraftingTableRegistry.addEntry(new StonePickaxeStoneCraftEntry());
        craftCraftingTableRegistry.addEntry(new StoneShovelCraftEntry());
        craftCraftingTableRegistry.addEntry(new TorchCraftEntry());
        craftCraftingTableRegistry.addEntry(new FurnaceCraftEntry());
        craftCraftingTableRegistry.addEntry(new EnergyBatteryCraftEntry());
        craftCraftingTableRegistry.addEntry(new EnergyCableCraftEntry());
        craftCraftingTableRegistry.addEntry(new WrenchCraftEntry());
        craftCraftingTableRegistry.addEntry(new WoodenSwordCraftEntry());
        craftCraftingTableRegistry.addEntry(new StoneSwordCraftEntry());
        craftCraftingTableRegistry.addEntry(new EnergyGeneratorCraftEntry());

        // craft furnace
        Registry<FurnaceCraftShapeless> craftFurnaceRegistry = Registry.createRegistry(crafts, "furnace", "furnaceRecipes");
        craftFurnaceRegistry.addEntry(new IronIngotCraftEntry());
        craftFurnaceRegistry.addEntry(new TinIngotCraftEntry());
        craftFurnaceRegistry.addEntry(new CopperIngotCraftEntry());
        craftFurnaceRegistry.addEntry(new GoldIngotCraftEntry());
        craftFurnaceRegistry.addEntry(new ChickenNuggetsCraftEntry());

        // quests
        Registry<QuestEntry> questRegistry = Registry.createRegistry(modRegistry, "quests", "quests");
        questRegistry.addEntry(new FirstQuestEntry());
        questRegistry.addEntry(new KnowYouWorldQuestEntry());
        questRegistry.addEntry(new FirstResourcesQuestEntry());
        questRegistry.addEntry(new FirstCraftQuestEntry());
        questRegistry.addEntry(new CraftingExpansionQuestEntry());
        questRegistry.addEntry(new FirstToolQuestEntry());
        questRegistry.addEntry(new ThermalExpansionQuestEntry());
        questRegistry.addEntry(new GetReadyForElectricityQuestEntry());
        questRegistry.addEntry(new FirstEnergyCableQuestEntry());
        questRegistry.addEntry(new FirstEnergyBatteryQuestEntry());
        questRegistry.addEntry(new FirstEnergyGeneratorQuestEntry());


        // quests category
        Registry<QuestCategory> questCategoryRegistry = Registry.createRegistry(modRegistry, "questsCategory", "questsCategory");
        questCategoryRegistry.addEntry(new Tier0QuestCategory());

        // quests manager
        Registry<Entry> questManagerRegistry = Registry.createRegistry(modRegistry, "questManager");
        questManagerRegistry.addEntry(new QuestManagerEntry());

        // entity edit factory
        Registry<EditEntityFactory> entityEditFactory = Registry.createRegistry(modRegistry, "editEntity");
        entityEditFactory.addEntry(new EditEntityFactory());

        // options
        Registry<Entry> optionsRegistry = Registry.createRegistry(modRegistry, "options");
        // option loader
        optionsRegistry.addEntry(new OptionLoader());

        // server options
        Registry<OptionServerEntry<?>> serverOptionRegistry = Registry.createRegistry(optionsRegistry, "server", "customOptions", "serverOptions");
        serverOptionRegistry.addEntry(new AuthServerBaseUrlServerOptionEntry());
        serverOptionRegistry.addEntry(new RenderDistanceOptionEntry());
        serverOptionRegistry.addEntry(new VerifyAccessTokenUrnOptionEntry());
        serverOptionRegistry.addEntry(new VerifyTokenOptionEntry());
        serverOptionRegistry.addEntry(new MaxDstSpawnPlayerOptionEntry());
        serverOptionRegistry.addEntry(new MaxEnemyCountOptionEntry());
        serverOptionRegistry.addEntry(new MinDstSpawnPlayerOptionEntry());
        serverOptionRegistry.addEntry(new NaturalSpawnCoolDownOptionEntry());
        serverOptionRegistry.addEntry(new EnemyFocusPlayerDstOptionEntry());
        serverOptionRegistry.addEntry(new EnemyDispawnDstOptionEntry());

        // client options
        Registry<OptionClientEntry<?>> clientOptionRegistry = Registry.createRegistry(optionsRegistry, "client", "customOptions", "clientOptions");
        clientOptionRegistry.addEntry(new AuthServerBaseUrlClientOptionEntry());
        clientOptionRegistry.addEntry(new CreateAccessTokenUrnOptionEntry());
        clientOptionRegistry.addEntry(new FpsOptionEntry(context));
        clientOptionRegistry.addEntry(new FullScreenOptionEntry(context));
        clientOptionRegistry.addEntry(new InventoryBlurOptionEntry());
        clientOptionRegistry.addEntry(new KeyDropItemOptionEntry());
        clientOptionRegistry.addEntry(new KeyMoveDownOptionEntry());
        clientOptionRegistry.addEntry(new KeyMoveLeftOptionEntry());
        clientOptionRegistry.addEntry(new KeyMoveRightOptionEntry());
        clientOptionRegistry.addEntry(new KeyMoveUpOptionEntry());
        clientOptionRegistry.addEntry(new KeyOpenQuestOptionEntry());
        clientOptionRegistry.addEntry(new OpenInventoryOptionEntry());
        clientOptionRegistry.addEntry(new SoundOptionEntry());
        clientOptionRegistry.addEntry(new TiledTextureOptionEntry());
        clientOptionRegistry.addEntry(new VerifyLoginUrn());
        clientOptionRegistry.addEntry(new VsyncOptionEntry(context));

        // mobs
        Registry<MobEntry> mobsRegistry = Registry.createRegistry(modRegistry, "mobs");
        mobsRegistry.addEntry(new ZombieMobEntry());
        mobsRegistry.addEntry(new ChickenMobEntry());

        // commands admin
        Registry<Entry> commandRegistry = Registry.createRegistry(modRegistry, "commands");
        commandRegistry.addEntry(new CommandLoader());
        commandRegistry.addEntry(new MasterServerCommandSupplierEntry());

        // sub commands
        Registry<CommandEntry> subCommandRegistry = Registry.createRegistry(commandRegistry, "subCommands", "customCommands");
        subCommandRegistry.addEntry(new EchoCommandEntry());
        subCommandRegistry.addEntry(new GiveCommandEntry());
        subCommandRegistry.addEntry(new NotifyCommandEntry());
        subCommandRegistry.addEntry(new RuntimeInfoCommandEntry());
        subCommandRegistry.addEntry(new StopCommandEntry());
        subCommandRegistry.addEntry(new TpCommandEntry());
        subCommandRegistry.addEntry(new WhereCommandEntry());

        // dumps
        subCommandRegistry.addEntry(new DumpCommandEntry());
        subCommandRegistry.addEntry(new DumpChunksCommandEntry());
        subCommandRegistry.addEntry(new DumpEntitiesCommandEntry());
        subCommandRegistry.addEntry(new DumpInventoryCommandEntry());
        subCommandRegistry.addEntry(new DumpPlayersCommandEntry());
        subCommandRegistry.addEntry(new DumpSubscriptionCommandEntry());
        subCommandRegistry.addEntry(new DumpItemsCommandEntry());

        // options
        subCommandRegistry.addEntry(new OptionsCommandEntry());
        subCommandRegistry.addEntry(new OptionsGetCommandEntry());
        subCommandRegistry.addEntry(new OptionsListCommandEntry());
        subCommandRegistry.addEntry(new OptionsReloadCommandEntry());
        subCommandRegistry.addEntry(new OptionsResetCommandEntry());
        subCommandRegistry.addEntry(new OptionsSaveCommandEntry());
        subCommandRegistry.addEntry(new OptionsSetCommandEntry());

        // time
        subCommandRegistry.addEntry(new TimeCommandEntry());
        subCommandRegistry.addEntry(new TimeGetCommandEntry());
        subCommandRegistry.addEntry(new TimeSetCommandEntry());

    }
}
