package ch.realmtech.server.mod.quests;

import ch.realmtech.server.registry.QuestEntry;

public class GetReadyForElectricityQuestEntry extends QuestEntry {
    public GetReadyForElectricityQuestEntry() {
        super("GetReadyForElectricity", "tier 0", "Get ready for electricity", """
                Introducing electricity into your world will introduce a lot of complexity.
                The first complexity will be the orientation of the cells.
                To manipulate this orientation, use a wrench.
                To craft the wrench, place a iron ingot in the left and top corner and iron ingot in the middle and bottom middle. Like a Y shape.
                The notary cells that can be turned are the cables and batteries.
                To turn the cell, the location of your click is important: click on the right-hand part of the cell to turn it to the right.
                Click on the left-hand side of the cell to turn it to the left. And the same for top and bottom.
                """);
    }
}
