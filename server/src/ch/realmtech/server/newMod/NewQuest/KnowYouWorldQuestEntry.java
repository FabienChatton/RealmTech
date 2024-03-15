package ch.realmtech.server.newMod.NewQuest;

import ch.realmtech.server.newRegistry.NewQuestEntry;

public class KnowYouWorldQuestEntry extends NewQuestEntry {
    public KnowYouWorldQuestEntry() {
        super("KnowYourWorld", "Know Your World", """
                RealmTech is a game that procedurally generates the Terran. You can explore endlessly.
                If you can't find a certain resource near your location, explore further.
                RealmTech is also a multiplayer game, so a friend can join your world using the multiplayer menu.
                There's a day/night cycle in the game, so you can use torches to light your way.
                These are crafted with a stick and a coal on top.
                For the moment, there are no monsters, so your life is not in danger.
                """);
    }
}
