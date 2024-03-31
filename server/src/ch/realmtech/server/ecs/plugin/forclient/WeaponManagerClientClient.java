package ch.realmtech.server.ecs.plugin.forclient;

import java.util.UUID;

public interface WeaponManagerClientClient {
    void playerHasJustShoot(UUID playerUuid);
}
