package ch.realmtech.server.mod.quests;

import ch.realmtech.server.registry.QuestEntry;

public class FirstEnergyBatteryQuestEntry extends QuestEntry {
    public FirstEnergyBatteryQuestEntry() {
        super("FirstEnergyBattery", "tier 0", "First Energy Battery", """
                To create our first energy generator, we must first create a battery.
                The energy battery allows energy to be stored.
                The direction of the battery is important. To change the direction, use a wrench.
                The energy output is the direction you selected. All other directions are energy inputs.
                To recharge the battery, connect a cable connected to a power input port connected to a power source.
                As energy sources, there is an energy generator or a battery with its output side connected.
                """);
    }
}
