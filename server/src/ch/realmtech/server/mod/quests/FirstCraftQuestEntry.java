package ch.realmtech.server.mod.quests;

import ch.realmtech.server.registry.QuestEntry;

public class FirstCraftQuestEntry extends QuestEntry {
    public FirstCraftQuestEntry() {
        super("FirstCraft", "tier 0", "First craft", """
                Now that you've got your first resources, it's time to use them to make new items.
                Open your inventory with e. You'll find our inventory with a crafting table.
                Left-click on your logs and put them in the crafting table to make planks.
                Planks can be placed on the floor.
                """);
        setPos(128, 0);
        setDependQuestsFqrn("realmtech.quests.FirstResources");
    }

    @Override
    public String getTextureRegionForIcon() {
        return "table-craft-01";
    }
}
