package ch.realmtech.server.mod.quests;

import ch.realmtech.server.registry.QuestEntry;

public class ThermalExpansionQuestEntry extends QuestEntry {
    public ThermalExpansionQuestEntry() {
        super("ThermalExpansion", "tier 0", "Thermal Expansion", """
                Now it's time to process your ores. To do this, you need a furnace.
                To get your first furnace, you need 8 stones, and craft the furnace in a crafting table by placing
                the 8 stones in the edges of the crafting inventory to make an "O" shape.
                To use the furnace, right-click on the furnace cell. You'll see a slot at the top for the item to be melted.
                At the bottom is the fuel slot and the slot on the right is the recipe result.
                Try putting a copper ore in the top slot and a coal in the bottom slot.
                You'll see the arrow filling up, indicating that the firing process has gone smoothly.
                Once the arrow is full, the result will be a copper ingot.
                The best fuel at this stage of the game is coal, but you can also use wood logs.
                """);
    }
}
