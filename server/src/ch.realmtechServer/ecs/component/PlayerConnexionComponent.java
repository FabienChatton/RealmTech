package ch.realmtechServer.ecs.component;

import ch.realmtechServer.divers.Position;
import com.artemis.Component;
import com.artemis.annotations.EntityId;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerConnexionComponent extends Component {
    public Channel channel;
    public UUID uuid;
    public int[] ancienChunkPos = null;
    @EntityId
    @Deprecated
    public int[] infChunks;
    public List<Position> chunkPoss;

    public PlayerConnexionComponent set(Channel channel, UUID uuid) {
        this.channel = channel;
        this.uuid = uuid;
        chunkPoss = new ArrayList<>();
        return this;
    }
}
