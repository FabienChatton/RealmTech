package ch.realmtech.server.ecs.plugin.server;

import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import ch.realmtech.server.ecs.system.*;
import ch.realmtech.server.ia.IaTestSystem;
import com.artemis.WorldConfigurationBuilder;

public class SystemsAdminServer extends SystemsAdminCommun {
    public final IaSystemServer iaSystemServer;
    public final ItemManagerServer itemManagerServer;
    public final PlayerMouvementSystemServer playerMouvementSystemServer;
    public final MapSystemServer mapSystemServer;
    public final IaTestSystem iaTestSystem;
    public final CraftingSystem craftingSystem;
    public final CraftingManager craftingManager;
    public final PickUpOnGroundItemSystem pickUpOnGroundItemSystem;
    public final Box2dFrotementSystem box2dFrotementSystem;
    public final ItemOnGroundPosSyncSystem itemOnGroundPosSyncSystem;
    public final UpdateBox2dWithPosition updateBox2dWithPosition;
    public final ItemBeingPickAnimationSystem itemBeingPickAnimationSystem;
    public final PhysiqueWorldStepSystem physiqueWorldStepSystem;
    public final PickerGroundItemContactSystem pickerGroundItemContactSystem;
    public final PlayerManagerServer playerManagerServer;
    public final TimeSystem timeSystem;
    public final PhysicEntityIaTestSystem physicEntitySystem;
    public final PlayerSyncSystem playerSyncSystem;
    public final IaIsFocusPlayerSystem iaIsFocusPlayerSystem;
    public final FurnaceSystem furnaceSystem;

    public SystemsAdminServer() {
        iaSystemServer = new IaSystemServer();
        itemManagerServer = new ItemManagerServer();
        playerMouvementSystemServer = new PlayerMouvementSystemServer();
        mapSystemServer = new MapSystemServer();
        iaTestSystem = new IaTestSystem();
        physicEntitySystem = new PhysicEntityIaTestSystem();
        playerSyncSystem = new PlayerSyncSystem();
        iaIsFocusPlayerSystem = new IaIsFocusPlayerSystem();
        furnaceSystem = new FurnaceSystem();

        craftingSystem = new CraftingSystem();
        craftingManager = new CraftingManager();
        pickUpOnGroundItemSystem = new PickUpOnGroundItemSystem();

        timeSystem = new TimeSystem();
        box2dFrotementSystem = new Box2dFrotementSystem();
        itemOnGroundPosSyncSystem = new ItemOnGroundPosSyncSystem();
        updateBox2dWithPosition = new UpdateBox2dWithPosition();
        itemBeingPickAnimationSystem = new ItemBeingPickAnimationSystem();
        physiqueWorldStepSystem = new PhysiqueWorldStepSystem();
        pickerGroundItemContactSystem = new PickerGroundItemContactSystem();
        playerManagerServer = new PlayerManagerServer();
    }


    public void setup(WorldConfigurationBuilder b) {
        super.setup(b);
        b.with(
                iaSystemServer,
                itemManagerServer,
                playerMouvementSystemServer,
                mapSystemServer,
                iaTestSystem,
                physicEntitySystem,
                playerSyncSystem,
                iaIsFocusPlayerSystem,
                furnaceSystem,

                craftingSystem,
                craftingManager,
                pickUpOnGroundItemSystem,

                timeSystem,
                box2dFrotementSystem,
                itemOnGroundPosSyncSystem,
                updateBox2dWithPosition,
                itemBeingPickAnimationSystem,
                physiqueWorldStepSystem,
                pickerGroundItemContactSystem,
                playerManagerServer
        );
    }
}
