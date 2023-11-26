package ch.realmtech.server.craft;

import ch.realmtech.server.ServerContext;
import com.artemis.World;

public interface CraftingStrategyItf {
    byte ID_LENGTH = Byte.BYTES;
    boolean consumeCraftingStrategy(ServerContext serverContext, World world, CraftResult craftResult, int id);
    byte getId();
}
