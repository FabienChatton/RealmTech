package ch.realmtech.server.energy;

import ch.realmtech.server.ecs.ExecuteOnContext;
import ch.realmtech.server.ecs.component.EnergyBatteryComponent;
import ch.realmtech.server.ecs.component.EnergyGeneratorComponent;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import ch.realmtech.server.level.cell.EditEntity;
import ch.realmtech.server.serialize.SerializerController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EnergyGeneratorEditEntity implements EditEntity {
    private final static Map<UUID, PersisteEnergyGeneratorInfo> PERSISTE_ENERGY_GENERATOR_INFO_MAP = new HashMap<>();
    private final UUID energyGeneratorUuid;
    private final int remainingTickToBurn;
    private final long stored;
    private final long capacity;

    private EnergyGeneratorEditEntity() {
        this(UUID.randomUUID(), 0, 0, 10_000);
    }

    public static EnergyGeneratorEditEntity createDefault() {
        return new EnergyGeneratorEditEntity();
    }

    public EnergyGeneratorEditEntity(UUID energyGeneratorUuid, int remainingTickToBurn, long stored, long capacity) {
        this.energyGeneratorUuid = energyGeneratorUuid;
        this.remainingTickToBurn = remainingTickToBurn;
        this.stored = stored;
        this.capacity = capacity;
    }

    @Override
    public void createEntity(ExecuteOnContext executeOnContext, int entityId) {
        executeOnContext.onCommun((world) -> {
            SystemsAdminCommun systemsAdminCommun = world.getRegistered("systemsAdmin");

            systemsAdminCommun.uuidComponentManager.createRegisteredComponent(energyGeneratorUuid, entityId);
            world.edit(entityId).create(EnergyGeneratorComponent.class).set(remainingTickToBurn);
            world.edit(entityId).create(EnergyBatteryComponent.class).set(stored, capacity, EnergyBatteryComponent.EnergyBatteryRole.EMITTER_ONLY);
            systemsAdminCommun.cellPaddingManager.addOrCreate(entityId, world.getRegistered(SerializerController.class).getEnergyGeneratorSerializerController());
        });
        executeOnContext.onClient((systemsAdminClientForClient, world) -> {
            if (!PERSISTE_ENERGY_GENERATOR_INFO_MAP.containsKey(energyGeneratorUuid)) {
                PERSISTE_ENERGY_GENERATOR_INFO_MAP.put(energyGeneratorUuid, new PersisteEnergyGeneratorInfo(UUID.randomUUID()));
            }
            PersisteEnergyGeneratorInfo persisteEnergyGeneratorInfo = PERSISTE_ENERGY_GENERATOR_INFO_MAP.get(energyGeneratorUuid);
            systemsAdminClientForClient.getEnergyBatteryIconSystem().createEnergyGeneratorIcon(entityId, persisteEnergyGeneratorInfo.uuidInventoryIcon());
        });
    }

    @Override
    public void deleteEntity(ExecuteOnContext executeOnContext, int entityId) {
        executeOnContext.onClient((systemsAdminClientForClient, world) -> {
            systemsAdminClientForClient.getEnergyBatteryIconSystem().deleteGeneratorBatteryIcons(entityId);
        });
    }

    @Override
    public void replaceEntity(ExecuteOnContext executeOnContext, int entityId) {
        deleteEntity(executeOnContext, entityId);
    }

    private record PersisteEnergyGeneratorInfo(UUID uuidInventoryIcon) { }
}
