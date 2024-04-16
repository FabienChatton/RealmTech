package ch.realmtech.server.mod.quests;

import ch.realmtech.server.registry.QuestEntry;

public class FirstQuestEntry extends QuestEntry {
    public FirstQuestEntry() {
        super("TheBeginning", "tier 0", "The beginning", """
                Welcome to {WAIT} {COLOR=RED}{SHAKE}RealmTech{ENDSHAKE}{ENDCOLOR}.
                Like you, RealmTech is at the beginning of a {RAINBOW}long journey{ENDRAINBOW}.
                Your goal in this version is to {COLOR=RED}{SLOW}fill a battery with energy{ENDCOLOR}.
                This requires many small steps. This quest book will help you get there.
                """);
        setPos(0, 0);
    }

    @Override
    public String getTextureRegionForIcon() {
        return "buche-01";
    }
}
