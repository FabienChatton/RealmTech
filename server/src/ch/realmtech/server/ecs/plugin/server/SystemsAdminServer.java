package ch.realmtech.server.ecs.plugin.server;

import ch.realmtech.server.ecs.plugin.commun.ContextType;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import ch.realmtech.server.ecs.system.*;
import ch.realmtech.server.ia.IaTestSystem;
import ch.realmtech.server.registry.InvalideEvaluate;
import ch.realmtech.server.registry.Registry;
import com.artemis.WorldConfigurationBuilder;

public class SystemsAdminServer extends SystemsAdminCommun {
    private IaSystemServer iaSystemServer;
    private ItemManagerServer itemManagerServer;
    private PlayerMouvementSystemServer playerMouvementSystemServer;
    private MapSystemServer mapSystemServer;
    private IaTestSystem iaTestSystem;
    private CraftingSystem craftingSystem;
    private CraftingManager craftingManager;
    private PickUpOnGroundItemSystem pickUpOnGroundItemSystem;
    private Box2dFrotementSystem box2dFrotementSystem;
    private ItemOnGroundPosSyncSystem itemOnGroundPosSyncSystem;
    private UpdateBox2dWithPosition updateBox2dWithPosition;
    private ItemBeingPickAnimationSystem itemBeingPickAnimationSystem;
    private PhysiqueWorldStepSystem physiqueWorldStepSystem;
    private PickerGroundItemContactSystem pickerGroundItemContactSystem;
    private PlayerManagerServer playerManagerServer;
    private TimeSystem timeSystem;
    private PhysicEntityIaTestSystem physicEntitySystem;
    private PlayerSyncSystem playerSyncSystem;
    private IaIsFocusPlayerSystem iaIsFocusPlayerSystem;
    private FurnaceSystem furnaceSystem;
    private EnergyBatterySystem energyBatterySystem;
    private EnergyGeneratorSystem energyGeneratorSystem;
    private DirtyCellSystem dirtyCellSystem;
    private PlayerSubscriptionSystem playerSubscriptionSystem;
    private PlayerMobContactSystem playerMobContactSystem;
    private WeaponRayManager weaponRayManager;

    public SystemsAdminServer() {
        super("SystemsAdminServer");
    }

    @Override
    public void evaluate(Registry<?> rootRegistry) throws InvalideEvaluate {
        super.evaluate(rootRegistry);
        setIaSystemServer(new IaSystemServer());
        setItemManagerServer(new ItemManagerServer());
        setPlayerMouvementSystemServer(new PlayerMouvementSystemServer());
        setMapSystemServer(new MapSystemServer());
        setIaTestSystem(new IaTestSystem());
        setPhysicEntitySystem(new PhysicEntityIaTestSystem());
        setPlayerSyncSystem(new PlayerSyncSystem());
        setIaIsFocusPlayerSystem(new IaIsFocusPlayerSystem());
        setFurnaceSystem(new FurnaceSystem());
        setEnergyBatterySystem(new EnergyBatterySystem());
        setEnergyGeneratorSystem(new EnergyGeneratorSystem());
        setPlayerMobContactSystem(new PlayerMobContactSystem());
        setWeaponRayManager(new WeaponRayManager());

        setCraftingSystem(new CraftingSystem());
        setCraftingManager(new CraftingManager());
        setPickUpOnGroundItemSystem(new PickUpOnGroundItemSystem());

        setTimeSystem(new TimeSystem());
        setBox2dFrotementSystem(new Box2dFrotementSystem());
        setItemOnGroundPosSyncSystem(new ItemOnGroundPosSyncSystem());
        setUpdateBox2dWithPosition(new UpdateBox2dWithPosition());
        setItemBeingPickAnimationSystem(new ItemBeingPickAnimationSystem());
        setPhysiqueWorldStepSystem(new PhysiqueWorldStepSystem());
        setPickerGroundItemContactSystem(new PickerGroundItemContactSystem());
        setPlayerManagerServer(new PlayerManagerServer());
        setDirtyCellSystem(new DirtyCellSystem());
        setPlayerSubscriptionSystem(new PlayerSubscriptionSystem());
    }

    public void setup(WorldConfigurationBuilder b) {
        super.setup(b);
        b.with(
                getIaSystemServer(),
                getItemManagerServer(),
                getPlayerMouvementSystemServer(),
                getMapSystemServer(),
                getIaTestSystem(),
                getPhysicEntitySystem(),
                getPlayerSyncSystem(),
                getIaIsFocusPlayerSystem(),
                getFurnaceSystem(),
                getEnergyBatterySystem(),
                getEnergyGeneratorSystem(),
                getPlayerMobContactSystem(),
                getWeaponRayManager(),

                getCraftingSystem(),
                getCraftingManager(),
                getPickUpOnGroundItemSystem(),

                getTimeSystem(),
                getBox2dFrotementSystem(),
                getItemOnGroundPosSyncSystem(),
                getUpdateBox2dWithPosition(),
                getItemBeingPickAnimationSystem(),
                getPhysiqueWorldStepSystem(),
                getPickerGroundItemContactSystem(),
                getPlayerManagerServer(),
                getDirtyCellSystem(),
                getPlayerSubscriptionSystem()
        );
    }

    @Override
    public void onContextType(ContextType contextType, Runnable runnable) {
        if (contextType == ContextType.SERVER) {
            runnable.run();
        }
    }

    public IaSystemServer getIaSystemServer() {
        return iaSystemServer;
    }

    public void setIaSystemServer(IaSystemServer iaSystemServer) {
        this.iaSystemServer = iaSystemServer;
    }

    public ItemManagerServer getItemManagerServer() {
        return itemManagerServer;
    }

    public void setItemManagerServer(ItemManagerServer itemManagerServer) {
        this.itemManagerServer = itemManagerServer;
    }

    public PlayerMouvementSystemServer getPlayerMouvementSystemServer() {
        return playerMouvementSystemServer;
    }

    public void setPlayerMouvementSystemServer(PlayerMouvementSystemServer playerMouvementSystemServer) {
        this.playerMouvementSystemServer = playerMouvementSystemServer;
    }

    public MapSystemServer getMapSystemServer() {
        return mapSystemServer;
    }

    public void setMapSystemServer(MapSystemServer mapSystemServer) {
        this.mapSystemServer = mapSystemServer;
    }

    public IaTestSystem getIaTestSystem() {
        return iaTestSystem;
    }

    public void setIaTestSystem(IaTestSystem iaTestSystem) {
        this.iaTestSystem = iaTestSystem;
    }

    public CraftingSystem getCraftingSystem() {
        return craftingSystem;
    }

    public void setCraftingSystem(CraftingSystem craftingSystem) {
        this.craftingSystem = craftingSystem;
    }

    public CraftingManager getCraftingManager() {
        return craftingManager;
    }

    public void setCraftingManager(CraftingManager craftingManager) {
        this.craftingManager = craftingManager;
    }

    public PickUpOnGroundItemSystem getPickUpOnGroundItemSystem() {
        return pickUpOnGroundItemSystem;
    }

    public void setPickUpOnGroundItemSystem(PickUpOnGroundItemSystem pickUpOnGroundItemSystem) {
        this.pickUpOnGroundItemSystem = pickUpOnGroundItemSystem;
    }

    public Box2dFrotementSystem getBox2dFrotementSystem() {
        return box2dFrotementSystem;
    }

    public void setBox2dFrotementSystem(Box2dFrotementSystem box2dFrotementSystem) {
        this.box2dFrotementSystem = box2dFrotementSystem;
    }

    public ItemOnGroundPosSyncSystem getItemOnGroundPosSyncSystem() {
        return itemOnGroundPosSyncSystem;
    }

    public void setItemOnGroundPosSyncSystem(ItemOnGroundPosSyncSystem itemOnGroundPosSyncSystem) {
        this.itemOnGroundPosSyncSystem = itemOnGroundPosSyncSystem;
    }

    public UpdateBox2dWithPosition getUpdateBox2dWithPosition() {
        return updateBox2dWithPosition;
    }

    public void setUpdateBox2dWithPosition(UpdateBox2dWithPosition updateBox2dWithPosition) {
        this.updateBox2dWithPosition = updateBox2dWithPosition;
    }

    public ItemBeingPickAnimationSystem getItemBeingPickAnimationSystem() {
        return itemBeingPickAnimationSystem;
    }

    public void setItemBeingPickAnimationSystem(ItemBeingPickAnimationSystem itemBeingPickAnimationSystem) {
        this.itemBeingPickAnimationSystem = itemBeingPickAnimationSystem;
    }

    public PhysiqueWorldStepSystem getPhysiqueWorldStepSystem() {
        return physiqueWorldStepSystem;
    }

    public void setPhysiqueWorldStepSystem(PhysiqueWorldStepSystem physiqueWorldStepSystem) {
        this.physiqueWorldStepSystem = physiqueWorldStepSystem;
    }

    public PickerGroundItemContactSystem getPickerGroundItemContactSystem() {
        return pickerGroundItemContactSystem;
    }

    public void setPickerGroundItemContactSystem(PickerGroundItemContactSystem pickerGroundItemContactSystem) {
        this.pickerGroundItemContactSystem = pickerGroundItemContactSystem;
    }

    public PlayerManagerServer getPlayerManagerServer() {
        return playerManagerServer;
    }

    public void setPlayerManagerServer(PlayerManagerServer playerManagerServer) {
        this.playerManagerServer = playerManagerServer;
    }

    public TimeSystem getTimeSystem() {
        return timeSystem;
    }

    public void setTimeSystem(TimeSystem timeSystem) {
        this.timeSystem = timeSystem;
    }

    public PhysicEntityIaTestSystem getPhysicEntitySystem() {
        return physicEntitySystem;
    }

    public void setPhysicEntitySystem(PhysicEntityIaTestSystem physicEntitySystem) {
        this.physicEntitySystem = physicEntitySystem;
    }

    public PlayerSyncSystem getPlayerSyncSystem() {
        return playerSyncSystem;
    }

    public void setPlayerSyncSystem(PlayerSyncSystem playerSyncSystem) {
        this.playerSyncSystem = playerSyncSystem;
    }

    public IaIsFocusPlayerSystem getIaIsFocusPlayerSystem() {
        return iaIsFocusPlayerSystem;
    }

    public void setIaIsFocusPlayerSystem(IaIsFocusPlayerSystem iaIsFocusPlayerSystem) {
        this.iaIsFocusPlayerSystem = iaIsFocusPlayerSystem;
    }

    public FurnaceSystem getFurnaceSystem() {
        return furnaceSystem;
    }

    public void setFurnaceSystem(FurnaceSystem furnaceSystem) {
        this.furnaceSystem = furnaceSystem;
    }

    public EnergyBatterySystem getEnergyBatterySystem() {
        return energyBatterySystem;
    }

    public void setEnergyBatterySystem(EnergyBatterySystem energyBatterySystem) {
        this.energyBatterySystem = energyBatterySystem;
    }

    public EnergyGeneratorSystem getEnergyGeneratorSystem() {
        return energyGeneratorSystem;
    }

    public void setEnergyGeneratorSystem(EnergyGeneratorSystem energyGeneratorSystem) {
        this.energyGeneratorSystem = energyGeneratorSystem;
    }

    public DirtyCellSystem getDirtyCellSystem() {
        return dirtyCellSystem;
    }

    public void setDirtyCellSystem(DirtyCellSystem dirtyCellSystem) {
        this.dirtyCellSystem = dirtyCellSystem;
    }

    public PlayerSubscriptionSystem getPlayerSubscriptionSystem() {
        return playerSubscriptionSystem;
    }

    public void setPlayerSubscriptionSystem(PlayerSubscriptionSystem playerSubscriptionSystem) {
        this.playerSubscriptionSystem = playerSubscriptionSystem;
    }

    public PlayerMobContactSystem getPlayerMobContactSystem() {
        return playerMobContactSystem;
    }

    public void setPlayerMobContactSystem(PlayerMobContactSystem playerMobContactSystem) {
        this.playerMobContactSystem = playerMobContactSystem;
    }

    public WeaponRayManager getWeaponRayManager() {
        return weaponRayManager;
    }

    public void setWeaponRayManager(WeaponRayManager weaponRayManager) {
        this.weaponRayManager = weaponRayManager;
    }
}
