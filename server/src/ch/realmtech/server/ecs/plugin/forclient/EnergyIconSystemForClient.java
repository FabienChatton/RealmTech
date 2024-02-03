package ch.realmtech.server.ecs.plugin.forclient;

import java.util.UUID;

public interface EnergyIconSystemForClient {
    void createEnergyGeneratorIcon(int motherId, UUID iconInventoryUuid);

    void deleteGeneratorBatteryIcons(int entityId);
}
