package ch.realmtech.server.level;

import ch.realmtech.server.mod.ClientContext;

import java.util.function.BiConsumer;

public interface RightClickInteraction extends BiConsumer<ClientContext, Integer> { }
