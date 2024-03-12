package ch.realmtech.server.newMod.NewQuest;

import ch.realmtech.server.newRegistry.NewQuestEntry;

public class FirstToolQuestEntry extends NewQuestEntry {
    public FirstToolQuestEntry() {
        super("firstTool", "First Tool", """
                With your new crafting table, you can use it to craft your first tools. Let's build a pickaxe.
                You'll need 2 wooden sticks and 3 wooden planks.
                To obtain the sticks, you'll need to align 2 wooden planks vertically in a crafting table.
                Now, the recipe for the pickaxe is to align 3 planks of wood horizontally at the top of the craft inventory.
                Put one stick in the middle of the inventory, and another stick down the middle.
                Now you can mine stones with your pickaxe.
                """);
    }
}
