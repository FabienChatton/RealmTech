package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ecs.component.UuidComponent;
import com.artemis.*;
import com.artemis.annotations.Wire;
import com.artemis.utils.IntBag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class UuidComponentManager extends Manager {
    @Wire
    private ComponentMapper<UuidComponent> mUuid;

    /**
     * Get the {@link Entity} id of a registered {@link UuidComponent} in this world.
     * @param uuid The uuid of the {@link Component}.
     * @return The {@link Entity} id or -1 if not found.
     */
    public int getRegisteredComponent(UUID uuid) {
        IntBag entities = world.getAspectSubscriptionManager().get(Aspect.all(UuidComponent.class)).getEntities();
        return getRegisteredComponent(uuid, entities);
    }

    /**
     * Get the {@link UuidComponent} of this {@link Entity}.
     * @param entityId The entity id who as this uuid component.
     * @return the {@link UuidComponent} of null if not found.
     */
    public UuidComponent getRegisteredComponent(int entityId) {
        return mUuid.get(entityId);
    }

    /**
     * Get the {@link Entity} id of a registered {@link Component} in this world.
     * For faster research, specify the component to search.
     * @param uuid The playerUuid of the {@link Component}.
     * @param componentClass The desired class with a uuid.
     * @return The {@link Entity} id or -1 if not found.
     */
    @SafeVarargs
    public final int getRegisteredComponent(UUID uuid, Class<? extends Component>... componentClass) {
        List<Class<? extends Component>> list = new ArrayList<>(componentClass.length + 1);
        list.addAll(Arrays.asList(componentClass));
        list.add(UuidComponent.class);
        synchronized (this) {
            IntBag entities = world.getAspectSubscriptionManager().get(Aspect.all(list)).getEntities();
            return getRegisteredComponent(uuid, entities);
        }
    }

    /**
     * Add a {@link UuidComponent} to this entity.
     * @param uuid The {@link UUID} to add a this entity.
     * @param entityId The entityId to add the {@link UuidComponent}
     * @throws IllegalStateException If the entity id has already a uuid component.
     * @return the entity id.
     */
    public UuidComponent createRegisteredComponent(UUID uuid, int entityId) throws IllegalStateException {
        synchronized (this) {
            if (mUuid.get(entityId) != null) throw new IllegalStateException("This entity has already a uuid component");
            return world.edit(entityId).create(UuidComponent.class).set(uuid);
        }
    }

    private int getRegisteredComponent(UUID uuid, IntBag entities) {
        synchronized (this) {
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
        }
        return -1;
    }
}
