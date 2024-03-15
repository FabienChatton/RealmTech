package ch.realmtech.server.newMod.NewQuest;

import ch.realmtech.server.newRegistry.NewQuestEntry;

public class FirstResourcesQuestEntry extends NewQuestEntry {
    public FirstResourcesQuestEntry() {
        super("FirstResources", "First resources", """
                As in {COLOR=YELLOW}GregTech{ENDCOLOR}, your first goal is to {COLOR=RED}harvest wood{ENDCOLOR}.
                Hold down the left mouse button to break a tree cell to get some wood.
                To find out if you can harvest a resource, check if the text break with is green.
                """);
    }
}
