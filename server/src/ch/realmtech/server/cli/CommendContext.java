package ch.realmtech.server.cli;

import ch.realmtech.server.ecs.Context;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import ch.realmtech.server.registry.Registry;
import ch.realmtech.server.serialize.SerializerController;
import com.artemis.World;

public interface CommendContext {
    World getWorld();
    SerializerController getSerializerManagerController();

    Registry<?> getRootRegistry();

    Context getContext();

    SystemsAdminCommun getSystemAdmin();
}
