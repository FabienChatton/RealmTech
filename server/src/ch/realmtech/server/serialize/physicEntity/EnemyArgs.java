package ch.realmtech.server.serialize.physicEntity;

import java.util.UUID;

public record EnemyArgs(UUID uuid, float x, float y, int enemyEntryId) {
}
