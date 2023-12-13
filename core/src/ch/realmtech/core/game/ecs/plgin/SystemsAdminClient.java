package ch.realmtech.core.game.ecs.plgin;

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
    public final UpdateBox2dWithPosition updateBox2dWithPosition;
    public final CameraFollowPlayerSystem cameraFollowPlayerSystem;
    public final MapRendererSystem mapRendererSystem;
    public final CellBeingMineRenderSystem cellBeingMineRenderSystem;
    public final CellHoverEtWailaSystem cellHoverEtWailaSystem;
    public final TextureRenderer textureRenderer;
    public final PlayerInventorySystem playerInventorySystem;
    public final ItemBarManager itemBarManager;
    public final CellBeingMineSystem cellBeingMineSystem;
    public final LightSystem lightSystem;
    public final TimeSystemSimulation timeSystemSimulation;

    public SystemsAdminClient() {
        playerManagerClient = new PlayerManagerClient();
        itemManagerClient = new ItemManagerClient();
        playerInputSystem = new PlayerInputSystem();
        playerMouvementTextureSystem = new PlayerMouvementTextureSystem();
        playerMouvementSystem = new PlayerMouvementSystem();
        playerTextureAnimated = new PlayerTextureAnimated();
        updateBox2dWithPosition = new UpdateBox2dWithPosition();
        timeSystemSimulation = new TimeSystemSimulation();
        // render
        cameraFollowPlayerSystem = new CameraFollowPlayerSystem();
        mapRendererSystem = new MapRendererSystem();
        cellBeingMineRenderSystem = new CellBeingMineRenderSystem();
        cellHoverEtWailaSystem = new CellHoverEtWailaSystem();
        textureRenderer = new TextureRenderer();

        playerInventorySystem = new PlayerInventorySystem();
        itemBarManager = new ItemBarManager();

        lightSystem = new LightSystem();


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
                // render
                playerMouvementTextureSystem,
                playerMouvementSystem,
                playerTextureAnimated,
                updateBox2dWithPosition,
                cameraFollowPlayerSystem,
                mapRendererSystem,
                textureRenderer,
                lightSystem,

                // ui
                cellBeingMineRenderSystem,
                cellHoverEtWailaSystem,
                playerInventorySystem,
                itemBarManager,
                cellBeingMineSystem
        );
    }
}
