package ch.realmtech.server.mod.commandes;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.mod.commandes.masterCommand.MasterServerCommandNew;
import ch.realmtech.server.registry.Entry;
import ch.realmtech.server.registry.InvalideEvaluate;
import ch.realmtech.server.registry.Registry;

import java.io.PrintWriter;

public class MasterServerCommandSupplierEntry extends Entry {

    public MasterServerCommandSupplierEntry() {
        super("MasterServerCommandSupplier");
    }

    @Override
    public void evaluate(Registry<?> rootRegistry) throws InvalideEvaluate {

    }

    public MasterServerCommandNew get(ServerContext serverContext, PrintWriter output, int senderId) {
        return new MasterServerCommandNew(serverContext, output, senderId);
    }
}
