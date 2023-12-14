package ch.realmtech.server.serialize.physicEntity;

import java.util.UUID;

public record PhysicEntityArgs(UUID uuid, float x, float y, byte flag) {
}
