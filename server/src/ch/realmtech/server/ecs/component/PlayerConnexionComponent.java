package ch.realmtech.server.ecs.component;

import ch.realmtech.server.divers.Position;
import com.artemis.Component;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerConnexionComponent extends Component {
    public Channel channel;
    public UUID uuid;
    public int[] ancienChunkPos = null;
    public List<Position> chunkPoss;

    public PlayerConnexionComponent set(Channel channel, UUID uuid, UUID mainInventoryUUID) {
        this.channel = channel;
        this.uuid = uuid;
        chunkPoss = new ArrayList<>();
        return this;
    }
}
