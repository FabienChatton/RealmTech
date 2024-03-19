package ch.realmtech.server.mod.quests;

import ch.realmtech.server.registry.QuestEntry;

public class CraftingExpansionQuestEntry extends QuestEntry {
    public CraftingExpansionQuestEntry() {
        super("CraftingExpansion", "First craft", """
                The first utility item you'll craft is a crafting table.
                The crafting table extends the size of the crafting table in the player's inventory.
                With the crafting table, the size of the crafting inventory is 3x3.
                With this enlarged size, more complex items and machines are available to craft.
                To make a crafting table, open the inventory and put 2x2 planks of wood in the crafting table in the player's inventory.
                After crafting the table, close the inventory and right-click to place it on the floor.
                Finally, interact with the craft table by right-clicking on the cell.
                """);
    }
}
