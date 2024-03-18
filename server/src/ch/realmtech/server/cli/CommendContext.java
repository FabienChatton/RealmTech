package ch.realmtech.server.cli;

import ch.realmtech.server.ecs.Context;
import ch.realmtech.server.newRegistry.NewRegistry;
import ch.realmtech.server.serialize.SerializerController;
import com.artemis.World;

public interface CommendContext {
    World getWorld();
    SerializerController getSerializerManagerController();

    NewRegistry<?> getRootRegistry();

    Context getContext();
}
