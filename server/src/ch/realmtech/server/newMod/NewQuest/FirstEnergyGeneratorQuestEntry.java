package ch.realmtech.server.newMod.NewQuest;

import ch.realmtech.server.newRegistry.NewQuestEntry;

public class FirstEnergyGeneratorQuestEntry extends NewQuestEntry {
    public FirstEnergyGeneratorQuestEntry() {
        super("FirstEnergyGenerator", "First Energy Generator", """
                Just having energy cables doesn't help. They take their usefulness to transport energy.
                But to transport energy, you have to have energy in the first place.
                That's what this quest is all about. Your first source of energy will be an energy generator.
                To craft the energy generator, place a furnace with an
                energy battery in the crafting inventory (regardless of item location).
                To create your first energy units, place coal in the furnace and the furnace's internal battery will fill up.
                """);
    }
}
