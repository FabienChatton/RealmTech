package ch.realmtech.server.cli;

import ch.realmtech.server.serialize.SerializerController;
import com.artemis.World;

public interface CommendContext {
    World getWorld();
    SerializerController getSerializerManagerController();
}
