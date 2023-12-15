package ch.realmtech.core.game.ecs.plugin;

import ch.realmtech.core.game.ecs.system.*;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import ch.realmtech.server.ecs.system.UpdateBox2dWithPosition;
import com.artemis.WorldConfigurationBuilder;

public class SystemsAdminClient extends SystemsAdminCommun {

    public final PlayerManagerClient playerManagerClient;
    public final ItemManagerClient itemManagerClient;
    public final PlayerInputSystem playerInputSystem;
    public final PlayerMouvementTextureSystem playerMouvementTextureSystem;
    public final PlayerMouvementSystem playerMouvementSystem;
    public final PlayerTextureAnimated playerTextureAnimated;
    public final IaManagerClient iaManagerClient;
    public final UpdateBox2dWithPosition updateBox2dWithPosition;
    public final CameraFollowPlayerSystem cameraFollowPlayerSystem;
    public final MapRendererSystem mapRendererSystem;
    public final CellBeingMineRenderSystem cellBeingMineRenderSystem;
    public final CellHoverEtWailaSystem cellHoverEtWailaSystem;
    public final TextureRenderer textureRenderer;
    public final PlayerInventorySystem playerInventorySystem;
    public final ItemBarManager itemBarManager;
    public final CellBeingMineSystem cellBeingMineSystem;
    public final LightCycleSystem lightCycleSystem;
    public final TimeSystemSimulation timeSystemSimulation;
    public final LightManager lightManager;

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
}
