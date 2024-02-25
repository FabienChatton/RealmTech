package ch.realmtech.server.ecs.system;

import ch.realmtech.server.uuid.UuidEntity;
import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.utils.Bag;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UuidEntityManager extends Manager {
    private final List<UuidEntity> uuidEntities;

    public UuidEntityManager() {
        uuidEntities = new ArrayList<>();
    }

    @Override
    protected void initialize() {
        super.initialize();
    }

    @Override
    public void deleted(Entity e) {
        UuidEntity uuidEntity = getUuidEntity(e.getId());
        if (uuidEntity != null) {
            synchronized (uuidEntities) {
                uuidEntities.remove(uuidEntity);
            }
        }
    }

    /**
     * Get the {@link Entity} id of a registered entity in this world.
     *
     * @param uuid The uuid of the {@link Entity}.
     * @return The {@link Entity} id or -1 if not found.
     */
    public int getEntityId(UUID uuid) {
        int entityId = -1;
        UuidEntity uuidEntity = getUuidEntity(uuid);
        if (uuidEntity != null) {
            entityId = uuidEntity.entityId();
        }
        return entityId;
    }

    /**
     * Get the {@link UUID} of this {@link Entity}.
     *
     * @param entityId The entity id who as this uuid component.
     * @return the {@link UUID} of null if not found.
     */
    public UUID getEntityUuid(int entityId) {
        UUID entityUuid = null;
        UuidEntity uuidEntity = getUuidEntity(entityId);
        if (uuidEntity != null) {
            entityUuid = uuidEntity.uuid();
        }
        return entityUuid;
    }

    private UuidEntity getUuidEntity(UUID uuid) {
        UuidEntity uuidEntityRet = null;
        synchronized (uuidEntities) {
            for (UuidEntity uuidEntity : uuidEntities) {
                if (uuidEntity.uuid().equals(uuid)) {
                    uuidEntityRet = uuidEntity;
                    break;
                }
            }
        }
        return uuidEntityRet;
    }

    private UuidEntity getUuidEntity(int entityId) {
        UuidEntity uuidEntityRet = null;
        synchronized (uuidEntities) {
            for (UuidEntity uuidEntity : uuidEntities) {
                if (uuidEntity.entityId() == entityId) {
                    uuidEntityRet = uuidEntity;
                    break;
                }
            }
        }
        return uuidEntityRet;
    }

    /**
     * Add a {@link UUID} to this entity.
     *
     * @param uuid     The {@link UUID} to add with this entity.
     * @param entityId The entityId to add.
     * @throws IllegalStateException If the entity id has already a uuid component.
     */
    public void registerEntityIdWithUuid(UUID uuid, int entityId) throws IllegalStateException {
        if (uuidEntities.stream().anyMatch((uuidEntity) -> uuidEntity.entityId() == entityId)) {
            String[] componentsClassName = getComponentsClassName(entityId);
            throw new IllegalStateException("This entity has already a uuid. entity components: " + String.join(",", componentsClassName) + " id: " + entityId + ", uuid: " + uuid);
        }
        if (uuidEntities.stream().anyMatch((uuidEntity) -> uuidEntity.uuid().equals(uuid))) {
            int alreadyEntityId = getEntityId(uuid);
            String[] componentsClassName = getComponentsClassName(alreadyEntityId);
            throw new IllegalStateException("This uuid has already ben registered: " + String.join(",", componentsClassName) + " id: " + entityId + ", uuid: " + uuid);
        }
        synchronized (uuidEntities) {
            uuidEntities.add(new UuidEntity(entityId, uuid));
        }
    }

    private String[] getComponentsClassName(int entityId) {
        Bag<Component> components = new Bag<>();
        world.getEntity(entityId).getComponents(components);
        Object[] componentsData = components.getData();
        String[] componentsClassName = new String[components.size()];
        for (int i = 0; i < components.size(); i++) {
            componentsClassName[i] = componentsData[i].getClass().getSimpleName();
        }
        return componentsClassName;
    }

    public void deleteRegisteredEntity(int entityId) {
        UuidEntity uuidEntity = getUuidEntity(entityId);
        uuidEntities.remove(uuidEntity);
    }
}
