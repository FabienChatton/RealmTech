package ch.realmtechServer.cli;

import ch.realmtechServer.ecs.component.InfChunkComponent;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.utils.IntBag;

import java.util.concurrent.Callable;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.ParentCommand;

@Command(name = "chunks", description = "dump all loaded chunk")
public class DumpChunksCommand implements Callable<Integer> {
    @ParentCommand
    DumpCommand dumpCommand;
    @Override
    public Integer call() throws Exception {
        ComponentMapper<InfChunkComponent> mChunk = dumpCommand.masterCommand.getWorld().getMapper(InfChunkComponent.class);
        IntBag chunkEntities = dumpCommand.masterCommand.getWorld().getAspectSubscriptionManager().get(Aspect.all(
                InfChunkComponent.class
        )).getEntities();
        int[] data = chunkEntities.getData();
        for (int i = 0; i < chunkEntities.size(); i++) {
            InfChunkComponent infChunkComponent = mChunk.get(data[i]);
            dumpCommand.masterCommand.output.println(infChunkComponent);
        }
        if (chunkEntities.size() == 0) dumpCommand.masterCommand.output.println("no chunk loaded");
        return 0;
    }
}
