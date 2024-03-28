package ch.realmtech.server.ecs.plugin.server;

import ch.realmtech.server.ecs.plugin.commun.ContextType;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import ch.realmtech.server.ecs.system.*;
import ch.realmtech.server.ia.IaTestSystem;
import ch.realmtech.server.registry.InvalideEvaluate;
import ch.realmtech.server.registry.Registry;

public class SystemsAdminServer extends SystemsAdminCommun {

    public SystemsAdminServer() {
        super("SystemsAdminServer");
    }

    @Override
    public void evaluate(Registry<?> rootRegistry) throws InvalideEvaluate {
        super.evaluate(rootRegistry);
        putCustomSystem(10, MobSystemServer.class, MobSystemServer::new);
        putCustomSystem(10, ItemManagerServer.class, ItemManagerServer::new);
        putCustomSystem(10, PlayerMouvementSystemServer.class, PlayerMouvementSystemServer::new);
        putCustomSystem(10, MapSystemServer.class, MapSystemServer::new);
        putCustomSystem(10, IaTestSystem.class, IaTestSystem::new);
        putCustomSystem(10, PhysicEntityIaTestSystem.class, PhysicEntityIaTestSystem::new);
        putCustomSystem(10, PlayerSyncSystem.class, PlayerSyncSystem::new);
        putCustomSystem(10, MobFocusPlayerSystem.class, MobFocusPlayerSystem::new);
        putCustomSystem(10, FurnaceSystem.class, FurnaceSystem::new);
        putCustomSystem(10, EnergyBatterySystem.class, EnergyBatterySystem::new);
        putCustomSystem(10, EnergyGeneratorSystem.class, EnergyGeneratorSystem::new);
        putCustomSystem(10, PlayerMobContactSystem.class, PlayerMobContactSystem::new);
        putCustomSystem(10, WeaponRayManager.class, WeaponRayManager::new);
        putCustomSystem(10, InvincibilitySystem.class, InvincibilitySystem::new);
        putCustomSystem(10, MobAttackCooldownSystem.class, MobAttackCooldownSystem::new);

        putCustomSystem(10, CraftingSystem.class, CraftingSystem::new);
        putCustomSystem(10, CraftingManager.class, CraftingManager::new);
        putCustomSystem(10, PickUpOnGroundItemSystem.class, PickUpOnGroundItemSystem::new);

        putCustomSystem(10, TimeSystem.class, TimeSystem::new);
        putCustomSystem(10, Box2dFrotementSystem.class, Box2dFrotementSystem::new);
        putCustomSystem(10, ItemOnGroundPosSyncSystem.class, ItemOnGroundPosSyncSystem::new);
        putCustomSystem(10, UpdateBox2dWithPosition.class, UpdateBox2dWithPosition::new);
        putCustomSystem(10, ItemBeingPickAnimationSystem.class, ItemBeingPickAnimationSystem::new);
        putCustomSystem(10, PhysiqueWorldStepSystem.class, PhysiqueWorldStepSystem::new);
        putCustomSystem(10, PickerGroundItemContactSystem.class, PickerGroundItemContactSystem::new);
        putCustomSystem(10, PlayerManagerServer.class, PlayerManagerServer::new);
        putCustomSystem(10, DirtyCellSystem.class, DirtyCellSystem::new);
        putCustomSystem(10, PlayerSubscriptionSystem.class, PlayerSubscriptionSystem::new);
    }

    @Override
    public void onContextType(ContextType contextType, Runnable runnable) {
        if (contextType == ContextType.SERVER) {
            runnable.run();
        }
    }

    public ItemManagerServer getItemManagerServer() {
        return getCustomSystem(ItemManagerServer.class);
    }

    public MobSystemServer getMobSystemServer() {
        return getCustomSystem(MobSystemServer.class);
    }

    public PlayerMouvementSystemServer getPlayerMouvementSystemServer() {
        return getCustomSystem(PlayerMouvementSystemServer.class);
    }

    public MapSystemServer getMapSystemServer() {
        return getCustomSystem(MapSystemServer.class);
    }

    public IaTestSystem getIaTestSystem() {
        return getCustomSystem(IaTestSystem.class);
    }

    public CraftingSystem getCraftingSystem() {
        return getCustomSystem(CraftingSystem.class);
    }

    public CraftingManager getCraftingManager() {
        return getCustomSystem(CraftingManager.class);
    }

    public PickUpOnGroundItemSystem getPickUpOnGroundItemSystem() {
        return getCustomSystem(PickUpOnGroundItemSystem.class);
    }

    public Box2dFrotementSystem getBox2dFrotementSystem() {
        return getCustomSystem(Box2dFrotementSystem.class);
    }

    public ItemOnGroundPosSyncSystem getItemOnGroundPosSyncSystem() {
        return getCustomSystem(ItemOnGroundPosSyncSystem.class);
    }

    public UpdateBox2dWithPosition getUpdateBox2dWithPosition() {
        return getCustomSystem(UpdateBox2dWithPosition.class);
    }

    public ItemBeingPickAnimationSystem getItemBeingPickAnimationSystem() {
        return getCustomSystem(ItemBeingPickAnimationSystem.class);
    }

    public PhysiqueWorldStepSystem getPhysiqueWorldStepSystem() {
        return getCustomSystem(PhysiqueWorldStepSystem.class);
    }

    public PickerGroundItemContactSystem getPickerGroundItemContactSystem() {
        return getCustomSystem(PickerGroundItemContactSystem.class);
    }

    public PlayerManagerServer getPlayerManagerServer() {
        return getCustomSystem(PlayerManagerServer.class);
    }

    public TimeSystem getTimeSystem() {
        return getCustomSystem(TimeSystem.class);
    }

    public PhysicEntityIaTestSystem getPhysicEntitySystem() {
        return getCustomSystem(PhysicEntityIaTestSystem.class);
    }

    public PlayerSyncSystem getPlayerSyncSystem() {
        return getCustomSystem(PlayerSyncSystem.class);
    }

    public MobFocusPlayerSystem getIaMobFocusPlayerSystem() {
        return getCustomSystem(MobFocusPlayerSystem.class);
    }

    public FurnaceSystem getFurnaceSystem() {
        return getCustomSystem(FurnaceSystem.class);
    }

    public EnergyBatterySystem getEnergyBatterySystem() {
        return getCustomSystem(EnergyBatterySystem.class);
    }

    public EnergyGeneratorSystem getEnergyGeneratorSystem() {
        return getCustomSystem(EnergyGeneratorSystem.class);
    }

    public DirtyCellSystem getDirtyCellSystem() {
        return getCustomSystem(DirtyCellSystem.class);
    }

    public PlayerSubscriptionSystem getPlayerSubscriptionSystem() {
        return getCustomSystem(PlayerSubscriptionSystem.class);
    }

    public PlayerMobContactSystem getPlayerMobContactSystem() {
        return getCustomSystem(PlayerMobContactSystem.class);
    }

    public WeaponRayManager getWeaponRayManager() {
        return getCustomSystem(WeaponRayManager.class);
    }

    public InvincibilitySystem getRayIgnoreSystem() {
        return getCustomSystem(InvincibilitySystem.class);
    }

    public MobAttackCooldownSystem getMobAttackCooldownSystem() {
        return getCustomSystem(MobAttackCooldownSystem.class);
    }
}
