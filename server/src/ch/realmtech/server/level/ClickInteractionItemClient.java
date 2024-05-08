package ch.realmtech.server.level;

import ch.realmtech.server.mod.ClientContext;

public interface ClickInteractionItemClient {
    void accept(ClientContext clientContext, ClickEventClient event, int cellId, int itemId);

    default ClickInteraction toClickInteraction(ClickEventClient event) {
        return (clientContext, cellId, itemId) -> accept(clientContext, event, cellId, itemId);
    }
}
