package ch.realmtech.core.game.ecs.plugin;

import ch.realmtech.core.game.ecs.system.*;
import ch.realmtech.server.ecs.plugin.commun.ContextType;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import ch.realmtech.server.ecs.plugin.forclient.SystemsAdminClientForClient;
import ch.realmtech.server.ecs.system.CraftingManager;
import ch.realmtech.server.ecs.system.UpdateBox2dWithPosition;
import ch.realmtech.server.registry.InvalideEvaluate;
import ch.realmtech.server.registry.Registry;
import com.artemis.WorldConfigurationBuilder;

public class SystemsAdminClient extends SystemsAdminCommun implements SystemsAdminClientForClient {

    private PlayerManagerClient playerManagerClient;
    private ItemManagerClient itemManagerClient;
    private PlayerInputSystem playerInputSystem;
    private MouvementSystem mouvementSystem;
    private PlayerMouvementSystem playerMouvementSystem;
    private TextureAnimatedSystem textureAnimatedSystem;
    private IaManagerClient iaManagerClient;
    private UpdateBox2dWithPosition updateBox2dWithPosition;
    private CameraFollowPlayerSystem cameraFollowPlayerSystem;
    private MapRendererSystem mapRendererSystem;
    private CellBeingMineRenderSystem cellBeingMineRenderSystem;
    private CellHoverSystem cellHoverSystem;
    private WailaSystem wailaSystem;
    private TextureRenderer textureRenderer;
    private PlayerInventorySystem playerInventorySystem;
    private ItemBarSystem itemBarSystem;
    private CellBeingMineSystem cellBeingMineSystem;
    private LightCycleSystem lightCycleSystem;
    private TimeSystemSimulation timeSystemSimulation;
    private LightManager lightManager;
    private FurnaceIconSystem furnaceIconSystem;
    private FurnaceSimulationSystem furnaceSimulationSystem;
    private CraftingManager craftingManager;
    private EnergyGeneratorClientSystem energyBatteryIconSystem;
    private InventoryNeiSystem inventoryNeiSystem;
    private GameStageBatchBeginSystem gameStageBatchBeginSystem;
    private GameStageBatchEndSystem gameStageBatchEndSystem;
    private TiledTextureSystem tiledTextureSystem;
    private PlayerFootStepSystem playerFootStepSystem;
    private QuestSystem questSystem;

    public SystemsAdminClient() {
        super("SystemsAdminClient");
    }

    @Override
    public void evaluate(Registry<?> rootRegistry) throws InvalideEvaluate {
        super.evaluate(rootRegistry);
        setPlayerManagerClient(new PlayerManagerClient());
        setItemManagerClient(new ItemManagerClient());
        setPlayerInputSystem(new PlayerInputSystem());
        setMouvementSystem(new MouvementSystem());
        setPlayerMouvementSystem(new PlayerMouvementSystem());
        setTextureAnimatedSystem(new TextureAnimatedSystem());
        setIaManagerClient(new IaManagerClient());
        setUpdateBox2dWithPosition(new UpdateBox2dWithPosition());
        setTimeSystemSimulation(new TimeSystemSimulation());
        setCellHoverSystem(new CellHoverSystem());
        setLightManager(new LightManager());
        setFurnaceIconSystem(new FurnaceIconSystem());
        setFurnaceSimulationSystem(new FurnaceSimulationSystem());
        setCraftingManager(new CraftingManager());
        setEnergyBatteryIconSystem(new EnergyGeneratorClientSystem());
        setTiledTextureSystem(new TiledTextureSystem());
        setPlayerFootStepSystem(new PlayerFootStepSystem());
        // render
        setGameStageBatchBeginSystem(new GameStageBatchBeginSystem());
        setCameraFollowPlayerSystem(new CameraFollowPlayerSystem());
        setMapRendererSystem(new MapRendererSystem());
        setCellBeingMineRenderSystem(new CellBeingMineRenderSystem());
        setWailaSystem(new WailaSystem());
        setTextureRenderer(new TextureRenderer());
        setInventoryNeiSystem(new InventoryNeiSystem());
        setGameStageBatchEndSystem(new GameStageBatchEndSystem());

        setPlayerInventorySystem(new PlayerInventorySystem());
        setItemBarSystem(new ItemBarSystem());
        setQuestSystem(new QuestSystem());

        setLightCycleSystem(new LightCycleSystem());


        // tick simulation
        setCellBeingMineSystem(new CellBeingMineSystem());
//        physiqueWorldStepSystem = new PhysiqueWorldStepSystem();
    }

    @Override
    public void setup(WorldConfigurationBuilder b) {
        super.setup(b);
        b.with(
                getPlayerManagerClient(),
                getItemManagerClient(),
                getPlayerInputSystem(),
                getTimeSystemSimulation(),
                getIaManagerClient(),
                getLightManager(),
                getFurnaceIconSystem(),
                getFurnaceSimulationSystem(),
                getCraftingManager(),
                getEnergyBatteryIconSystem(),
                getMouvementSystem(),
                getPlayerMouvementSystem(),
                getTextureAnimatedSystem(),
                getUpdateBox2dWithPosition(),
                getCameraFollowPlayerSystem(),
                getTiledTextureSystem(),
                getPlayerFootStepSystem(),

                // render
                getGameStageBatchBeginSystem(),
                getMapRendererSystem(),
                getCellHoverSystem(),
                getTextureRenderer(),
                getCellBeingMineRenderSystem(),
                getGameStageBatchEndSystem(),
                getLightCycleSystem(),

                // ui
                getWailaSystem(),
                getQuestSystem(),
                getPlayerInventorySystem(),
                getInventoryNeiSystem(),
                getItemBarSystem(),
                getCellBeingMineSystem()
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
        return getMouvementSystem();
    }

    public PlayerMouvementSystem getPlayerMouvementSystem() {
        return playerMouvementSystem;
    }

    public TextureAnimatedSystem getPlayerTextureAnimated() {
        return getTextureAnimatedSystem();
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
        return getWailaSystem();
    }

    public TextureRenderer getTextureRenderer() {
        return textureRenderer;
    }

    public PlayerInventorySystem getPlayerInventorySystem() {
        return playerInventorySystem;
    }

    public ItemBarSystem getItemBarManager() {
        return getItemBarSystem();
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
        return getQuestSystem();
    }

    @Override
    public void onContextType(ContextType contextType, Runnable runnable) {
        if (contextType == ContextType.CLIENT) {
            runnable.run();
        }
    }

    public void setPlayerManagerClient(PlayerManagerClient playerManagerClient) {
        this.playerManagerClient = playerManagerClient;
    }

    public void setItemManagerClient(ItemManagerClient itemManagerClient) {
        this.itemManagerClient = itemManagerClient;
    }

    public void setPlayerInputSystem(PlayerInputSystem playerInputSystem) {
        this.playerInputSystem = playerInputSystem;
    }

    public MouvementSystem getMouvementSystem() {
        return mouvementSystem;
    }

    public void setMouvementSystem(MouvementSystem mouvementSystem) {
        this.mouvementSystem = mouvementSystem;
    }

    public void setPlayerMouvementSystem(PlayerMouvementSystem playerMouvementSystem) {
        this.playerMouvementSystem = playerMouvementSystem;
    }

    public TextureAnimatedSystem getTextureAnimatedSystem() {
        return textureAnimatedSystem;
    }

    public void setTextureAnimatedSystem(TextureAnimatedSystem textureAnimatedSystem) {
        this.textureAnimatedSystem = textureAnimatedSystem;
    }

    public void setIaManagerClient(IaManagerClient iaManagerClient) {
        this.iaManagerClient = iaManagerClient;
    }

    public void setUpdateBox2dWithPosition(UpdateBox2dWithPosition updateBox2dWithPosition) {
        this.updateBox2dWithPosition = updateBox2dWithPosition;
    }

    public void setCameraFollowPlayerSystem(CameraFollowPlayerSystem cameraFollowPlayerSystem) {
        this.cameraFollowPlayerSystem = cameraFollowPlayerSystem;
    }

    public void setMapRendererSystem(MapRendererSystem mapRendererSystem) {
        this.mapRendererSystem = mapRendererSystem;
    }

    public void setCellBeingMineRenderSystem(CellBeingMineRenderSystem cellBeingMineRenderSystem) {
        this.cellBeingMineRenderSystem = cellBeingMineRenderSystem;
    }

    public CellHoverSystem getCellHoverSystem() {
        return cellHoverSystem;
    }

    public void setCellHoverSystem(CellHoverSystem cellHoverSystem) {
        this.cellHoverSystem = cellHoverSystem;
    }

    public WailaSystem getWailaSystem() {
        return wailaSystem;
    }

    public void setWailaSystem(WailaSystem wailaSystem) {
        this.wailaSystem = wailaSystem;
    }

    public void setTextureRenderer(TextureRenderer textureRenderer) {
        this.textureRenderer = textureRenderer;
    }

    public void setPlayerInventorySystem(PlayerInventorySystem playerInventorySystem) {
        this.playerInventorySystem = playerInventorySystem;
    }

    public ItemBarSystem getItemBarSystem() {
        return itemBarSystem;
    }

    public void setItemBarSystem(ItemBarSystem itemBarSystem) {
        this.itemBarSystem = itemBarSystem;
    }

    public void setCellBeingMineSystem(CellBeingMineSystem cellBeingMineSystem) {
        this.cellBeingMineSystem = cellBeingMineSystem;
    }

    public void setLightCycleSystem(LightCycleSystem lightCycleSystem) {
        this.lightCycleSystem = lightCycleSystem;
    }

    public void setTimeSystemSimulation(TimeSystemSimulation timeSystemSimulation) {
        this.timeSystemSimulation = timeSystemSimulation;
    }

    public void setLightManager(LightManager lightManager) {
        this.lightManager = lightManager;
    }

    public void setFurnaceIconSystem(FurnaceIconSystem furnaceIconSystem) {
        this.furnaceIconSystem = furnaceIconSystem;
    }

    public FurnaceSimulationSystem getFurnaceSimulationSystem() {
        return furnaceSimulationSystem;
    }

    public void setFurnaceSimulationSystem(FurnaceSimulationSystem furnaceSimulationSystem) {
        this.furnaceSimulationSystem = furnaceSimulationSystem;
    }

    public void setCraftingManager(CraftingManager craftingManager) {
        this.craftingManager = craftingManager;
    }

    public void setEnergyBatteryIconSystem(EnergyGeneratorClientSystem energyBatteryIconSystem) {
        this.energyBatteryIconSystem = energyBatteryIconSystem;
    }

    public InventoryNeiSystem getInventoryNeiSystem() {
        return inventoryNeiSystem;
    }

    public void setInventoryNeiSystem(InventoryNeiSystem inventoryNeiSystem) {
        this.inventoryNeiSystem = inventoryNeiSystem;
    }

    public GameStageBatchBeginSystem getGameStageBatchBeginSystem() {
        return gameStageBatchBeginSystem;
    }

    public void setGameStageBatchBeginSystem(GameStageBatchBeginSystem gameStageBatchBeginSystem) {
        this.gameStageBatchBeginSystem = gameStageBatchBeginSystem;
    }

    public GameStageBatchEndSystem getGameStageBatchEndSystem() {
        return gameStageBatchEndSystem;
    }

    public void setGameStageBatchEndSystem(GameStageBatchEndSystem gameStageBatchEndSystem) {
        this.gameStageBatchEndSystem = gameStageBatchEndSystem;
    }

    public void setTiledTextureSystem(TiledTextureSystem tiledTextureSystem) {
        this.tiledTextureSystem = tiledTextureSystem;
    }

    public PlayerFootStepSystem getPlayerFootStepSystem() {
        return playerFootStepSystem;
    }

    public void setPlayerFootStepSystem(PlayerFootStepSystem playerFootStepSystem) {
        this.playerFootStepSystem = playerFootStepSystem;
    }

    public QuestSystem getQuestSystem() {
        return questSystem;
    }

    public void setQuestSystem(QuestSystem questSystem) {
        this.questSystem = questSystem;
    }
}
