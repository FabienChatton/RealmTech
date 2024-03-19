package ch.realmtech.core.game.ecs.plugin;

import ch.realmtech.core.game.ecs.system.*;
import ch.realmtech.server.ecs.plugin.commun.ContextType;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import ch.realmtech.server.ecs.plugin.forclient.SystemsAdminClientForClient;
import ch.realmtech.server.ecs.system.CraftingManager;
import ch.realmtech.server.ecs.system.UpdateBox2dWithPosition;
import ch.realmtech.server.newRegistry.NewRegistry;
import com.artemis.WorldConfigurationBuilder;

public class SystemsAdminClient extends SystemsAdminCommun implements SystemsAdminClientForClient {

    private final PlayerManagerClient playerManagerClient;
    private final ItemManagerClient itemManagerClient;
    private final PlayerInputSystem playerInputSystem;
    private final MouvementSystem mouvementSystem;
    private final PlayerMouvementSystem playerMouvementSystem;
    private final TextureAnimatedSystem textureAnimatedSystem;
    private final IaManagerClient iaManagerClient;
    private final UpdateBox2dWithPosition updateBox2dWithPosition;
    private final CameraFollowPlayerSystem cameraFollowPlayerSystem;
    private final MapRendererSystem mapRendererSystem;
    private final CellBeingMineRenderSystem cellBeingMineRenderSystem;
    private final CellHoverSystem cellHoverSystem;
    private final WailaSystem wailaSystem;
    private final TextureRenderer textureRenderer;
    private final PlayerInventorySystem playerInventorySystem;
    private final ItemBarSystem itemBarSystem;
    private final CellBeingMineSystem cellBeingMineSystem;
    private final LightCycleSystem lightCycleSystem;
    private final TimeSystemSimulation timeSystemSimulation;
    private final LightManager lightManager;
    private final FurnaceIconSystem furnaceIconSystem;
    private final FurnaceSimulationSystem furnaceSimulationSystem;
    private final CraftingManager craftingManager;
    private final EnergyGeneratorClientSystem energyBatteryIconSystem;
    private final InventoryNeiSystem inventoryNeiSystem;
    private final GameStageBatchBeginSystem gameStageBatchBeginSystem;
    private final GameStageBatchEndSystem gameStageBatchEndSystem;
    private final TiledTextureSystem tiledTextureSystem;
    private final PlayerFootStepSystem playerFootStepSystem;
    private final QuestSystem questSystem;


    public SystemsAdminClient(NewRegistry<?> rootRegistry) {
        super(rootRegistry);
        playerManagerClient = new PlayerManagerClient();
        itemManagerClient = new ItemManagerClient();
        playerInputSystem = new PlayerInputSystem();
        mouvementSystem = new MouvementSystem();
        playerMouvementSystem = new PlayerMouvementSystem();
        textureAnimatedSystem = new TextureAnimatedSystem();
        iaManagerClient = new IaManagerClient();
        updateBox2dWithPosition = new UpdateBox2dWithPosition();
        timeSystemSimulation = new TimeSystemSimulation();
        cellHoverSystem = new CellHoverSystem();
        lightManager = new LightManager();
        furnaceIconSystem = new FurnaceIconSystem();
        furnaceSimulationSystem = new FurnaceSimulationSystem();
        craftingManager = new CraftingManager();
        energyBatteryIconSystem = new EnergyGeneratorClientSystem();
        tiledTextureSystem = new TiledTextureSystem();
        playerFootStepSystem = new PlayerFootStepSystem();
        // render
        gameStageBatchBeginSystem = new GameStageBatchBeginSystem();
        cameraFollowPlayerSystem = new CameraFollowPlayerSystem();
        mapRendererSystem = new MapRendererSystem();
        cellBeingMineRenderSystem = new CellBeingMineRenderSystem();
        wailaSystem = new WailaSystem();
        textureRenderer = new TextureRenderer();
        inventoryNeiSystem = new InventoryNeiSystem();
        gameStageBatchEndSystem = new GameStageBatchEndSystem();

        playerInventorySystem = new PlayerInventorySystem();
        itemBarSystem = new ItemBarSystem();
        questSystem = new QuestSystem();

        lightCycleSystem = new LightCycleSystem();


        // tick simulation
        cellBeingMineSystem = new CellBeingMineSystem();
//        physiqueWorldStepSystem = new PhysiqueWorldStepSystem();
    }

    @Override
    public void setup(WorldConfigurationBuilder b) {
        super.setup(b);
        b.with(
                playerManagerClient,
                itemManagerClient,
                playerInputSystem,
                timeSystemSimulation,
                iaManagerClient,
                lightManager,
                furnaceIconSystem,
                furnaceSimulationSystem,
                craftingManager,
                energyBatteryIconSystem,
                mouvementSystem,
                playerMouvementSystem,
                textureAnimatedSystem,
                updateBox2dWithPosition,
                cameraFollowPlayerSystem,
                tiledTextureSystem,
                playerFootStepSystem,

                // render
                gameStageBatchBeginSystem,
                mapRendererSystem,
                cellHoverSystem,
                textureRenderer,
                cellBeingMineRenderSystem,
                gameStageBatchEndSystem,
                lightCycleSystem,

                // ui
                wailaSystem,
                questSystem,
                playerInventorySystem,
                inventoryNeiSystem,
                itemBarSystem,
                cellBeingMineSystem
        );
    }

    public PlayerManagerClient getPlayerManagerClient() {
        return playerManagerClient;
    }

    public ItemManagerClient getItemManagerClient() {
        return itemManagerClient;
    }

    public PlayerInputSystem getPlayerInputSystem() {
        return playerInputSystem;
    }

    public MouvementSystem getPlayerMouvementTextureSystem() {
        return mouvementSystem;
    }

    public PlayerMouvementSystem getPlayerMouvementSystem() {
        return playerMouvementSystem;
    }

    public TextureAnimatedSystem getPlayerTextureAnimated() {
        return textureAnimatedSystem;
    }

    public IaManagerClient getIaManagerClient() {
        return iaManagerClient;
    }

    public UpdateBox2dWithPosition getUpdateBox2dWithPosition() {
        return updateBox2dWithPosition;
    }

    public CameraFollowPlayerSystem getCameraFollowPlayerSystem() {
        return cameraFollowPlayerSystem;
    }

    public MapRendererSystem getMapRendererSystem() {
        return mapRendererSystem;
    }

    public CellBeingMineRenderSystem getCellBeingMineRenderSystem() {
        return cellBeingMineRenderSystem;
    }

    public WailaSystem getCellHoverEtWailaSystem() {
        return wailaSystem;
    }

    public TextureRenderer getTextureRenderer() {
        return textureRenderer;
    }

    public PlayerInventorySystem getPlayerInventorySystem() {
        return playerInventorySystem;
    }

    public ItemBarSystem getItemBarManager() {
        return itemBarSystem;
    }

    public CellBeingMineSystem getCellBeingMineSystem() {
        return cellBeingMineSystem;
    }

    public LightCycleSystem getLightCycleSystem() {
        return lightCycleSystem;
    }

    public TimeSystemSimulation getTimeSystemSimulation() {
        return timeSystemSimulation;
    }

    public CraftingManager getCraftingManager() {
        return craftingManager;
    }

    @Override
    public LightManager getLightManager() {
        return lightManager;
    }

    @Override
    public FurnaceIconSystem getFurnaceIconSystem() {
        return furnaceIconSystem;
    }

    @Override
    public EnergyGeneratorClientSystem getEnergyBatteryIconSystem() {
        return energyBatteryIconSystem;
    }

    public TiledTextureSystem getTiledTextureSystem() {
        return tiledTextureSystem;
    }

    public QuestSystem getQuestManager() {
        return questSystem;
    }

    @Override
    public void onContextType(ContextType contextType, Runnable runnable) {
        if (contextType == ContextType.CLIENT) {
            runnable.run();
        }
    }
}
