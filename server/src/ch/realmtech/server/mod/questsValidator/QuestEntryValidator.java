package ch.realmtech.server.mod.questsValidator;

import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;

public interface QuestEntryValidator {
    boolean validate(SystemsAdminServer systemsAdminServer, int inventoryId);
}
