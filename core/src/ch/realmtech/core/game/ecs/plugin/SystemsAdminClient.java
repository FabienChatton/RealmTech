package ch.realmtech.core.game.ecs.plugin;

import ch.realmtech.core.game.ecs.system.*;
import ch.realmtech.server.ecs.plugin.commun.ContextType;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import ch.realmtech.server.ecs.plugin.forclient.HitManagerForClient;
import ch.realmtech.server.ecs.plugin.forclient.SystemsAdminClientForClient;
import ch.realmtech.server.ecs.system.CraftingManager;
import ch.realmtech.server.ecs.system.PlayerDeadSystem;
import ch.realmtech.server.ecs.system.UpdateBox2dWithPosition;
import ch.realmtech.server.registry.InvalideEvaluate;
import ch.realmtech.server.registry.Registry;

public class SystemsAdminClient extends SystemsAdminCommun implements SystemsAdminClientForClient {

    /*
    0-100       update game
    101         batch begin
    102-199     render game
    200         batch end
     */
    public SystemsAdminClient() {
        super("SystemsAdminClient");
    }

    @Override
    public void evaluate(Registry<?> rootRegistry) throws InvalideEvaluate {
        super.evaluate(rootRegistry);
        putCustomSystem(11, PlayerManagerClient.class, PlayerManagerClient::new);
        putCustomSystem(12, ItemManagerClient.class, ItemManagerClient::new);
        putCustomSystem(13, PlayerInputSystem.class, PlayerInputSystem::new);
        putCustomSystem(14, TimeSystemSimulation.class, TimeSystemSimulation::new);
        putCustomSystem(15, EnemyManagerClient.class, EnemyManagerClient::new);
        putCustomSystem(16, LightManager.class, LightManager::new);
        putCustomSystem(17, FurnaceIconSystem.class, FurnaceIconSystem::new);
        putCustomSystem(18, FurnaceSimulationSystem.class, FurnaceSimulationSystem::new);
        putCustomSystem(19, CraftingManager.class, CraftingManager::new);
        putCustomSystem(20, EnergyGeneratorClientSystem.class, EnergyGeneratorClientSystem::new);
        putCustomSystem(21, MouvementSystem.class, MouvementSystem::new);
        putCustomSystem(22, PlayerMouvementSystem.class, PlayerMouvementSystem::new);
        putCustomSystem(23, TextureAnimatedSystem.class, TextureAnimatedSystem::new);
        putCustomSystem(24, UpdateBox2dWithPosition.class, UpdateBox2dWithPosition::new);
        putCustomSystem(25, CameraFollowPlayerSystem.class, CameraFollowPlayerSystem::new);
        putCustomSystem(26, TiledTextureSystem.class, TiledTextureSystem::new);
        putCustomSystem(27, PlayerFootStepSystem.class, PlayerFootStepSystem::new);
        putCustomSystem(28, MobManagerClient.class, MobManagerClient::new);
        putCustomSystem(29, WeaponManagerClient.class, WeaponManagerClient::new);
        putCustomSystem(30, TickSlaveEmulationSystem.class, TickSlaveEmulationSystem::new);
        putCustomSystem(31, PlayerDeadSystem.class, PlayerDeadSystem::new);
        putCustomSystem(32, PlayerGameInteractionSystem.class, PlayerGameInteractionSystem::new);
        putCustomSystem(33, PlayerDeadClientManager.class, PlayerDeadClientManager::new);
        putCustomSystem(34, HitClientManager.class, HitClientManager::new);
        putCustomSystem(35, ConsoleCommandWrapperManager.class, ConsoleCommandWrapperManager::new);

        // render
        putCustomSystem(101, GameStageBatchBeginSystem.class, GameStageBatchBeginSystem::new);
        putCustomSystem(110, MapRendererSystem.class, MapRendererSystem::new);
        putCustomSystem(120, CellHoverSystem.class, CellHoverSystem::new);
        putCustomSystem(130, TextureRenderer.class, TextureRenderer::new);
        putCustomSystem(140, CellBeingMineRenderSystem.class, CellBeingMineRenderSystem::new);
        putCustomSystem(150, ParticleEffectsSystem.class, ParticleEffectsSystem::new);
        putCustomSystem(200, GameStageBatchEndSystem.class, GameStageBatchEndSystem::new);


        putCustomSystem(300, LightCycleSystem.class, LightCycleSystem::new);
        putCustomSystem(310, WailaSystem.class, WailaSystem::new);
        putCustomSystem(320, QuestPlayerSystem.class, QuestPlayerSystem::new);
        putCustomSystem(340, PlayerInventorySystem.class, PlayerInventorySystem::new);
        putCustomSystem(350, InventoryNeiSystem.class, InventoryNeiSystem::new);
        putCustomSystem(360, ItemBarSystem.class, ItemBarSystem::new);
        putCustomSystem(360, CellBeingMineSystem.class, CellBeingMineSystem::new);

    }

    @Override
    public void onContextType(ContextType contextType, Runnable runnable) {
        if (contextType == ContextType.CLIENT) {
            runnable.run();
        }
    }

    public PlayerManagerClient getPlayerManagerClient() {
        return getCustomSystem(PlayerManagerClient.class);
    }

    public ItemManagerClient getItemManagerClient() {
        return getCustomSystem(ItemManagerClient.class);
    }

    public PlayerInputSystem getPlayerInputSystem() {
        return getCustomSystem(PlayerInputSystem.class);
    }

    public MouvementSystem getPlayerMouvementTextureSystem() {
        return getCustomSystem(MouvementSystem.class);
    }

    public PlayerMouvementSystem getPlayerMouvementSystem() {
        return getCustomSystem(PlayerMouvementSystem.class);
    }

    public TextureAnimatedSystem getPlayerTextureAnimated() {
        return getCustomSystem(TextureAnimatedSystem.class);
    }

    @Override
    public HitManagerForClient getHitManagerForClient() {
        return getCustomSystem(HitClientManager.class);
    }

    public EnemyManagerClient getEnemyManagerClient() {
        return getCustomSystem(EnemyManagerClient.class);
    }

    public UpdateBox2dWithPosition getUpdateBox2dWithPosition() {
        return getCustomSystem(UpdateBox2dWithPosition.class);
    }

    public CameraFollowPlayerSystem getCameraFollowPlayerSystem() {
        return getCustomSystem(CameraFollowPlayerSystem.class);
    }

    public MapRendererSystem getMapRendererSystem() {
        return getCustomSystem(MapRendererSystem.class);
    }

    public CellBeingMineRenderSystem getCellBeingMineRenderSystem() {
        return getCustomSystem(CellBeingMineRenderSystem.class);
    }

    public WailaSystem getCellHoverEtWailaSystem() {
        return getCustomSystem(WailaSystem.class);
    }

    public TextureRenderer getTextureRenderer() {
        return getCustomSystem(TextureRenderer.class);
    }

    public PlayerInventorySystem getPlayerInventorySystem() {
        return getCustomSystem(PlayerInventorySystem.class);
    }

    public ItemBarSystem getItemBarManager() {
        return getCustomSystem(ItemBarSystem.class);
    }

    public CellBeingMineSystem getCellBeingMineSystem() {
        return getCustomSystem(CellBeingMineSystem.class);
    }

    public LightCycleSystem getLightCycleSystem() {
        return getCustomSystem(LightCycleSystem.class);
    }

    public TimeSystemSimulation getTimeSystemSimulation() {
        return getCustomSystem(TimeSystemSimulation.class);
    }

    public CraftingManager getCraftingManager() {
        return getCustomSystem(CraftingManager.class);
    }

    @Override
    public LightManager getLightManager() {
        return getCustomSystem(LightManager.class);
    }

    @Override
    public FurnaceIconSystem getFurnaceIconSystem() {
        return getCustomSystem(FurnaceIconSystem.class);
    }

    @Override
    public EnergyGeneratorClientSystem getEnergyBatteryIconSystem() {
        return getCustomSystem(EnergyGeneratorClientSystem.class);
    }

    public TiledTextureSystem getTiledTextureSystem() {
        return getCustomSystem(TiledTextureSystem.class);
    }

    public MouvementSystem getMouvementSystem() {
        return getCustomSystem(MouvementSystem.class);
    }

    public TextureAnimatedSystem getTextureAnimatedSystem() {
        return getCustomSystem(TextureAnimatedSystem.class);
    }

    public CellHoverSystem getCellHoverSystem() {
        return getCustomSystem(CellHoverSystem.class);
    }

    public WailaSystem getWailaSystem() {
        return getCustomSystem(WailaSystem.class);
    }

    public ItemBarSystem getItemBarSystem() {
        return getCustomSystem(ItemBarSystem.class);
    }

    public FurnaceSimulationSystem getFurnaceSimulationSystem() {
        return getCustomSystem(FurnaceSimulationSystem.class);
    }

    public InventoryNeiSystem getInventoryNeiSystem() {
        return getCustomSystem(InventoryNeiSystem.class);
    }

    public GameStageBatchBeginSystem getGameStageBatchBeginSystem() {
        return getCustomSystem(GameStageBatchBeginSystem.class);
    }

    public GameStageBatchEndSystem getGameStageBatchEndSystem() {
        return getCustomSystem(GameStageBatchEndSystem.class);
    }

    public PlayerFootStepSystem getPlayerFootStepSystem() {
        return getCustomSystem(PlayerFootStepSystem.class);
    }

    public QuestPlayerSystem getQuestPlayerSystem() {
        return getCustomSystem(QuestPlayerSystem.class);
    }

    public ParticleEffectsSystem getParticleEffectsSystem() {
        return getCustomSystem(ParticleEffectsSystem.class);
    }

    public MobManagerClient getMobManagerClient() {
        return getCustomSystem(MobManagerClient.class);
    }

    public TickSlaveEmulationSystem getTickSlaveEmulationSystem() {
        return getCustomSystem(TickSlaveEmulationSystem.class);
    }

    public ConsoleCommandWrapperManager getConsoleCommandWrapperManager() {
        return getCustomSystem(ConsoleCommandWrapperManager.class);
    }

    @Override
    public WeaponManagerClient getWeaponManagerClient() {
        return getCustomSystem(WeaponManagerClient.class);
    }

}
