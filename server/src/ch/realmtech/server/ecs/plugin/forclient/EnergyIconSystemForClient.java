package ch.realmtech.server.ecs.plugin.forclient;

public interface EnergyIconSystemForClient {
    void createEnergyGeneratorIcon(int motherId);

    void deleteGeneratorBatteryIcons(int entityId);
}
