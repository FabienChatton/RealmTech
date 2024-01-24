package ch.realmtech.server.level;

import ch.realmtech.server.mod.ClientContext;

public interface RightClickInteractionItemClient {
    void accept(ClientContext clientContext, RightClickEventClient event, int itemId, int cellTargetId);
    default RightClickInteraction toRightClickInteraction(RightClickEventClient event, int cellTargetId) {
        return (clientContext, integer) -> accept(clientContext, event, integer, cellTargetId);
    }
}
