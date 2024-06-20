package ch.realmtech.server.mod.commandes.dump;

import ch.realmtech.server.ecs.component.InfChunkComponent;
import ch.realmtech.server.registry.CommandEntry;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.utils.IntBag;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.ParentCommand;

@Command(name = "chunks", description = "dump all loaded chunk")
public class DumpChunksCommandEntry extends CommandEntry {
    public DumpChunksCommandEntry() {
        super("DumpChunks");
    }

    @ParentCommand
    public DumpCommandEntry dumpCommand;


    @Override
    public void run() {
        ComponentMapper<InfChunkComponent> mChunk = dumpCommand.masterCommand.getWorld().getMapper(InfChunkComponent.class);
        IntBag chunkEntities = dumpCommand.masterCommand.getWorld().getAspectSubscriptionManager().get(Aspect.all(
                InfChunkComponent.class
        )).getEntities();
        int[] data = chunkEntities.getData();
        for (int i = 0; i < chunkEntities.size(); i++) {
            InfChunkComponent infChunkComponent = mChunk.get(data[i]);
            dumpCommand.printlnVerbose(1, infChunkComponent);
        }
        if (chunkEntities.isEmpty()) dumpCommand.masterCommand.output.println("no chunk loaded");
        else dumpCommand.masterCommand.output.println("Chunk count: " + chunkEntities.size());
    }
}
