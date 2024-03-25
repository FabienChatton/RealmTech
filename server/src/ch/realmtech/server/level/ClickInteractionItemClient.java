package ch.realmtech.server.level;

import ch.realmtech.server.mod.ClientContext;

public interface ClickInteractionItemClient {
    void accept(ClientContext clientContext, ClickEventClient event, int itemId, int cellTargetId);

    default ClickInteraction toClickInteraction(ClickEventClient event, int cellTargetId) {
        return (clientContext, integer) -> accept(clientContext, event, integer, cellTargetId);
    }
}
