package ch.realmtech.server.ecs.plugin.server;

import ch.realmtech.server.ecs.plugin.commun.ContextType;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import ch.realmtech.server.ecs.system.*;
import ch.realmtech.server.ia.IaTestSystem;
import ch.realmtech.server.registry.Registry;
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
    public final EnergyBatterySystem energyBatterySystem;
    public final EnergyGeneratorSystem energyGeneratorSystem;
    public final DirtyCellSystem dirtyCellSystem;
    public final PlayerSubscriptionSystem playerSubscriptionSystem;
    public final PlayerMobContactSystem playerMobContactSystem;
    public final WeaponRayManager weaponRayManager;

    public SystemsAdminServer(Registry<?> rootRegistry) {
        super(rootRegistry);
        iaSystemServer = new IaSystemServer();
        itemManagerServer = new ItemManagerServer();
        playerMouvementSystemServer = new PlayerMouvementSystemServer();
        mapSystemServer = new MapSystemServer();
        iaTestSystem = new IaTestSystem();
        physicEntitySystem = new PhysicEntityIaTestSystem();
        playerSyncSystem = new PlayerSyncSystem();
        iaIsFocusPlayerSystem = new IaIsFocusPlayerSystem();
        furnaceSystem = new FurnaceSystem();
        energyBatterySystem = new EnergyBatterySystem();
        energyGeneratorSystem = new EnergyGeneratorSystem();
        playerMobContactSystem = new PlayerMobContactSystem();
        weaponRayManager = new WeaponRayManager();

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
        dirtyCellSystem = new DirtyCellSystem();
        playerSubscriptionSystem = new PlayerSubscriptionSystem();
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
                energyBatterySystem,
                energyGeneratorSystem,
                playerMobContactSystem,
                weaponRayManager,

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
                playerManagerServer,
                dirtyCellSystem,
                playerSubscriptionSystem
        );
    }

    @Override
    public void onContextType(ContextType contextType, Runnable runnable) {
        if (contextType == ContextType.SERVER) {
            runnable.run();
        }
    }
}
