package ch.realmtech.core.game.ecs.plugin;

import ch.realmtech.core.game.ecs.system.*;
import ch.realmtech.server.ecs.plugin.SystemsAdminClientForClient;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import ch.realmtech.server.ecs.system.UpdateBox2dWithPosition;
import com.artemis.WorldConfigurationBuilder;

public class SystemsAdminClient extends SystemsAdminCommun implements SystemsAdminClientForClient {

    private final PlayerManagerClient playerManagerClient;
    private final ItemManagerClient itemManagerClient;
    private final PlayerInputSystem playerInputSystem;
    private final PlayerMouvementTextureSystem playerMouvementTextureSystem;
    private final PlayerMouvementSystem playerMouvementSystem;
    private final PlayerTextureAnimated playerTextureAnimated;
    private final IaManagerClient iaManagerClient;
    private final UpdateBox2dWithPosition updateBox2dWithPosition;
    private final CameraFollowPlayerSystem cameraFollowPlayerSystem;
    private final MapRendererSystem mapRendererSystem;
    private final CellBeingMineRenderSystem cellBeingMineRenderSystem;
    private final CellHoverEtWailaSystem cellHoverEtWailaSystem;
    private final TextureRenderer textureRenderer;
    private final PlayerInventorySystem playerInventorySystem;
    private final ItemBarManager itemBarManager;
    private final CellBeingMineSystem cellBeingMineSystem;
    private final LightCycleSystem lightCycleSystem;
    private final TimeSystemSimulation timeSystemSimulation;
    private final LightManager lightManager;

    public SystemsAdminClient() {
        playerManagerClient = new PlayerManagerClient();
        itemManagerClient = new ItemManagerClient();
        playerInputSystem = new PlayerInputSystem();
        playerMouvementTextureSystem = new PlayerMouvementTextureSystem();
        playerMouvementSystem = new PlayerMouvementSystem();
        playerTextureAnimated = new PlayerTextureAnimated();
        iaManagerClient = new IaManagerClient();
        updateBox2dWithPosition = new UpdateBox2dWithPosition();
        timeSystemSimulation = new TimeSystemSimulation();
        lightManager = new LightManager();
        // render
        cameraFollowPlayerSystem = new CameraFollowPlayerSystem();
        mapRendererSystem = new MapRendererSystem();
        cellBeingMineRenderSystem = new CellBeingMineRenderSystem();
        cellHoverEtWailaSystem = new CellHoverEtWailaSystem();
        textureRenderer = new TextureRenderer();

        playerInventorySystem = new PlayerInventorySystem();
        itemBarManager = new ItemBarManager();

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
                // render
                playerMouvementTextureSystem,
                playerMouvementSystem,
                playerTextureAnimated,
                updateBox2dWithPosition,
                cameraFollowPlayerSystem,
                mapRendererSystem,
                textureRenderer,
                lightCycleSystem,

                // ui
                cellBeingMineRenderSystem,
                cellHoverEtWailaSystem,
                playerInventorySystem,
                itemBarManager,
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

    public PlayerMouvementTextureSystem getPlayerMouvementTextureSystem() {
        return playerMouvementTextureSystem;
    }

    public PlayerMouvementSystem getPlayerMouvementSystem() {
        return playerMouvementSystem;
    }

    public PlayerTextureAnimated getPlayerTextureAnimated() {
        return playerTextureAnimated;
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

    public CellHoverEtWailaSystem getCellHoverEtWailaSystem() {
        return cellHoverEtWailaSystem;
    }

    public TextureRenderer getTextureRenderer() {
        return textureRenderer;
    }

    public PlayerInventorySystem getPlayerInventorySystem() {
        return playerInventorySystem;
    }

    public ItemBarManager getItemBarManager() {
        return itemBarManager;
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

    @Override
    public LightManager getLightManager() {
        return lightManager;
    }
}
