package ch.realmtech.server.ecs.plugin.server;

import ch.realmtech.server.ecs.plugin.commun.ContextType;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import ch.realmtech.server.ecs.system.*;
import ch.realmtech.server.ia.IaTestSystem;
import ch.realmtech.server.registry.InvalideEvaluate;
import ch.realmtech.server.registry.Registry;

public class SystemsAdminServer extends SystemsAdminCommun {
    private MobSystemServer mobSystemServer;
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
    private MobFocusPlayerSystem mobFocusPlayerSystem;
    private FurnaceSystem furnaceSystem;
    private EnergyBatterySystem energyBatterySystem;
    private EnergyGeneratorSystem energyGeneratorSystem;
    private DirtyCellSystem dirtyCellSystem;
    private PlayerSubscriptionSystem playerSubscriptionSystem;
    private PlayerMobContactSystem playerMobContactSystem;
    private WeaponRayManager weaponRayManager;
    private InvincibilitySystem invincibilitySystem;
    private MobAttackCooldownSystem mobAttackCooldownSystem;

    public SystemsAdminServer() {
        super("SystemsAdminServer");
    }

    @Override
    public void evaluate(Registry<?> rootRegistry) throws InvalideEvaluate {
        super.evaluate(rootRegistry);
        setIaSystemServer(putCustomSystem(10, new MobSystemServer()));
        setItemManagerServer(putCustomSystem(10, new ItemManagerServer()));
        setPlayerMouvementSystemServer(putCustomSystem(10, new PlayerMouvementSystemServer()));
        setMapSystemServer(putCustomSystem(10, new MapSystemServer()));
        setIaTestSystem(putCustomSystem(10, new IaTestSystem()));
        setPhysicEntitySystem(putCustomSystem(10, new PhysicEntityIaTestSystem()));
        setPlayerSyncSystem(putCustomSystem(10, new PlayerSyncSystem()));
        setMobIsFocusPlayerSystem(putCustomSystem(10, new MobFocusPlayerSystem()));
        setFurnaceSystem(putCustomSystem(10, new FurnaceSystem()));
        setEnergyBatterySystem(putCustomSystem(10, new EnergyBatterySystem()));
        setEnergyGeneratorSystem(putCustomSystem(10, new EnergyGeneratorSystem()));
        setPlayerMobContactSystem(putCustomSystem(10, new PlayerMobContactSystem()));
        setWeaponRayManager(putCustomSystem(10, new WeaponRayManager()));
        setRayIgnoreSystem(putCustomSystem(10, new InvincibilitySystem()));
        setMobAttackCooldownSystem(putCustomSystem(10, new MobAttackCooldownSystem()));

        setCraftingSystem(putCustomSystem(10, new CraftingSystem()));
        setCraftingManager(putCustomSystem(10, new CraftingManager()));
        setPickUpOnGroundItemSystem(putCustomSystem(10, new PickUpOnGroundItemSystem()));

        setTimeSystem(putCustomSystem(10, new TimeSystem()));
        setBox2dFrotementSystem(putCustomSystem(10, new Box2dFrotementSystem()));
        setItemOnGroundPosSyncSystem(putCustomSystem(10, new ItemOnGroundPosSyncSystem()));
        setUpdateBox2dWithPosition(putCustomSystem(10, new UpdateBox2dWithPosition()));
        setItemBeingPickAnimationSystem(putCustomSystem(10, new ItemBeingPickAnimationSystem()));
        setPhysiqueWorldStepSystem(putCustomSystem(10, new PhysiqueWorldStepSystem()));
        setPickerGroundItemContactSystem(putCustomSystem(10, new PickerGroundItemContactSystem()));
        setPlayerManagerServer(putCustomSystem(10, new PlayerManagerServer()));
        setDirtyCellSystem(putCustomSystem(10, new DirtyCellSystem()));
        setPlayerSubscriptionSystem(putCustomSystem(10, new PlayerSubscriptionSystem()));
    }

    @Override
    public void onContextType(ContextType contextType, Runnable runnable) {
        if (contextType == ContextType.SERVER) {
            runnable.run();
        }
    }

    public MobSystemServer getIaSystemServer() {
        return mobSystemServer;
    }

    public void setIaSystemServer(MobSystemServer mobSystemServer) {
        this.mobSystemServer = mobSystemServer;
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

    public MobFocusPlayerSystem getIaMobFocusPlayerSystem() {
        return mobFocusPlayerSystem;
    }

    public void setMobIsFocusPlayerSystem(MobFocusPlayerSystem mobFocusPlayerSystem) {
        this.mobFocusPlayerSystem = mobFocusPlayerSystem;
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

    public MobSystemServer getMobSystemServer() {
        return mobSystemServer;
    }

    public void setMobSystemServer(MobSystemServer mobSystemServer) {
        this.mobSystemServer = mobSystemServer;
    }

    public InvincibilitySystem getRayIgnoreSystem() {
        return invincibilitySystem;
    }

    public void setRayIgnoreSystem(InvincibilitySystem invincibilitySystem) {
        this.invincibilitySystem = invincibilitySystem;
    }

    public MobAttackCooldownSystem getMobAttackCooldownSystem() {
        return mobAttackCooldownSystem;
    }

    public void setMobAttackCooldownSystem(MobAttackCooldownSystem mobAttackCooldownSystem) {
        this.mobAttackCooldownSystem = mobAttackCooldownSystem;
    }
}
