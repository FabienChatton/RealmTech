package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ecs.component.UuidComponent;
import com.artemis.*;
import com.artemis.annotations.Wire;
import com.artemis.utils.IntBag;

import java.util.UUID;

public class UuidComponentManager extends Manager {
    @Wire
    private ComponentMapper<UuidComponent> mUuid;

    /**
     * Get the {@link Entity} id of a registered {@link UuidComponent} in this world.
     * @param uuid The playerUuid of the {@link Component}.
     * @return The {@link Entity} id or -1 if not found.
     */
    public int getRegisteredComponent(UUID uuid) {
        IntBag entities = world.getAspectSubscriptionManager().get(Aspect.all(UuidComponent.class)).getEntities();
        return getRegisteredComponent(uuid, entities);
    }

    /**
     * Get the {@link Entity} id of a registered {@link Component} in this world.
     * For faster research, specify the component to search.
     * @param uuid The playerUuid of the {@link Component}.
     * @param componentClass The desired class with a playerUuid.
     * @return The {@link Entity} id or -1 if not found.
     */
    public int getRegisteredComponent(UUID uuid, Class<? extends Component> componentClass) {
        IntBag entities = world.getAspectSubscriptionManager().get(Aspect.all(UuidComponent.class, componentClass)).getEntities();
        return getRegisteredComponent(uuid, entities);
    }

    /**
     * Add a {@link UuidComponent} to this entity.
     * @param uuid The {@link UUID} to add a this entity.
     * @param entityId The entityId to add the {@link UuidComponent}
     * @return the entity id.
     */
    public UuidComponent createRegisteredComponent(UUID uuid, int entityId) {
        return world.edit(entityId).create(UuidComponent.class).set(uuid);
    }

    private int getRegisteredComponent(UUID uuid, IntBag entities) {
        int[] entitiesData = entities.getData();
        for (int i = 0; i < entities.size(); i++) {
            int entityId = entitiesData[i];
            UuidComponent uuidComponent = mUuid.get(entityId);
            if (uuidComponent != null) {
                if (uuid.equals(uuidComponent.getUuid())) {
                    return entityId;
                }
            }
        }
        return -1;
    }
}
