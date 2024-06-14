package ch.realmtech.server.mod;

import ch.realmtech.server.ecs.Context;
import ch.realmtech.server.registry.Registry;

/**
 * The entry point for a mod. A mod need to implement this interface to be loaded.
 * <p>
 * The mod class must be in the {@code realmtech.mod} package and the class must have the same
 * name as the modId (with capitalization).
 * For exemple: {@code realmtech.mod.ModDemo}.
 * <p>
 * The Class can have a {@link AssetsProvider} annotation.
 */
public interface ModInitializer {
    /**
     * Get the mod id of this mod.
     * This mod id will be used to give you the corresponding registry for this mod.
     *
     * @return The mod id.
     */
    String getModId();

    /**
     * Initialize the mod registry. This is where you put all yours sub-registry and entry in the mod id.
     * @param modRegistry The registry corresponding to this mod.
     * @param context Determine if the initialization is on the server-side or client-side.
     */
    void initializeModRegistry(Registry<?> modRegistry, Context context);
}
