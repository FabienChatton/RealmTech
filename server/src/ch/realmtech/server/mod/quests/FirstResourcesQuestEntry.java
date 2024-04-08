package ch.realmtech.server.mod.quests;

import ch.realmtech.server.registry.QuestEntry;

public class FirstResourcesQuestEntry extends QuestEntry {
    public FirstResourcesQuestEntry() {
        super("FirstResources", "tier 0", "First resources", """
                As in {COLOR=YELLOW}GregTech{ENDCOLOR}, your first goal is to {COLOR=RED}harvest wood{ENDCOLOR}.
                Hold down the left mouse button to break a tree cell to get some wood.
                To find out if you can harvest a resource, check if the text break with is green.
                """);

    }

    @Override
    public String getTextureRegionForIcon() {
        return "buche-01";
    }
}
