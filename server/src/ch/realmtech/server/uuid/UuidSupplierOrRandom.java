package ch.realmtech.server.uuid;

import java.util.UUID;
import java.util.function.Supplier;

public class UuidSupplierOrRandom implements Supplier<UUID> {
    private final UUID uuid;

    public UuidSupplierOrRandom(UUID uuid) {
        this.uuid = uuid;
    }

    public UuidSupplierOrRandom() {
        this.uuid = null;
    }


    @Override
    public UUID get() {
        return uuid != null
                ? uuid
                : UUID.randomUUID();
    }
}
