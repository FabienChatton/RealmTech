package ch.realmtech.server.newMod.NewQuest;

import ch.realmtech.server.newRegistry.NewQuestEntry;

public class FirstCraftQuestEntry extends NewQuestEntry {
    public FirstCraftQuestEntry() {
        super("FirstCraft", "First craft", """
                Now that you've got your first resources, it's time to use them to make new items.
                Open your inventory with e. You'll find our inventory with a crafting table.
                Left-click on your logs and put them in the crafting table to make planks.
                Planks can be placed on the floor.
                """);
    }
}
