package ch.realmtech.server.mod.quests;

import ch.realmtech.server.registry.QuestEntry;

public class FirstEnergyCableQuestEntry extends QuestEntry {
    public FirstEnergyCableQuestEntry() {
        super("FirstEnergyCable", "tier 0", "First Energy Cable", """
                Now that you have the ability to smelt ores, you can use the ingots to create
                the components needed for electrical installations.
                First, make copper electric cables with 3 copper ingots aligned horizontally.
                You can lay these cables on the floor,
                where they will be useful for connecting your electrical machines to your energy production.
                To connect the cables to each other and to the machines,
                right-click with a wrench on the side to which you wish to connect the cable.
                """);
    }
}
