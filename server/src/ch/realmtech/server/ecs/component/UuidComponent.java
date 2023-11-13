package ch.realmtech.server.ecs.component;

import com.artemis.Component;

import java.util.UUID;

public class UuidComponent extends Component {
    /** The uuid value */
    private UUID uuid;

    public UuidComponent set(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public String toString() {
        return String.format("%s", uuid);
    }

    @Override
    public boolean equals(Object obj) {
        return uuid.equals(obj);
    }
}
