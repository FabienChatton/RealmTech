package ch.realmtech.server.newMod.NewQuest;

import ch.realmtech.server.newRegistry.NewQuestEntry;

public class FirstQuestEntry extends NewQuestEntry {
    public FirstQuestEntry() {
        super("TheBeginning", "The beginning", """
                Welcome to {WAIT} {COLOR=RED}{SHAKE}RealmTech{ENDSHAKE}{ENDCOLOR}.
                Like you, RealmTech is at the beginning of a {RAINBOW}long journey{ENDRAINBOW}.
                Your goal in this version is to {COLOR=RED}{SLOW}fill a battery with energy{ENDCOLOR}.
                This requires many small steps. This quest book will help you get there.
                """);
    }
}
