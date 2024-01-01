package ch.realmtech.server.ecs.plugin.server;

import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import ch.realmtech.server.ecs.system.*;
import ch.realmtech.server.ia.IaTestSystem;
import com.artemis.WorldConfigurationBuilder;

public class SystemsAdminServer extends SystemsAdminCommun {
    public final IaManagerServer iaManagerServer;
    public final ItemManagerServer itemManagerServer;
    public final PlayerMouvementSystemServer playerMouvementSystemServer;
    public final MapSystemServer mapSystemServer;
    public final IaTestSystem iaTestSystem;
    public final CraftingPlayerSystem craftingPlayerSystem;
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

    public SystemsAdminServer() {
        iaManagerServer = new IaManagerServer();
        itemManagerServer = new ItemManagerServer();
        playerMouvementSystemServer = new PlayerMouvementSystemServer();
        mapSystemServer = new MapSystemServer();
        iaTestSystem = new IaTestSystem();
        physicEntitySystem = new PhysicEntityIaTestSystem();
        playerSyncSystem = new PlayerSyncSystem();

        craftingPlayerSystem = new CraftingPlayerSystem();
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
                iaManagerServer,
                itemManagerServer,
                playerMouvementSystemServer,
                mapSystemServer,
                iaTestSystem,
                physicEntitySystem,
                playerSyncSystem,

                craftingPlayerSystem,
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
