package ch.realmtech.server.cli;

import ch.realmtech.server.datactrl.OptionCtrl;
import ch.realmtech.server.serialize.SerializerController;
import com.artemis.World;

import java.io.IOException;

public interface CommendContext {
    World getWorld();
    SerializerController getSerializerManagerController();
    OptionCtrl getOptionCtrl();
    void reloadOption() throws IOException;
}
