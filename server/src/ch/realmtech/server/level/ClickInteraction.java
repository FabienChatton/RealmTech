package ch.realmtech.server.level;

import ch.realmtech.server.mod.ClientContext;

public interface ClickInteraction {
    void accept(ClientContext clientContext, int cellId, int itemId);
}
