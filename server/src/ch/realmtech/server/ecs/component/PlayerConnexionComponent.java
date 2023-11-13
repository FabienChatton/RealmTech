package ch.realmtech.server.ecs.component;

import ch.realmtech.server.divers.Position;
import com.artemis.Component;
import com.artemis.annotations.EntityId;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;

public class PlayerConnexionComponent extends Component {
    public Channel channel;
    public int[] ancienChunkPos = null;
    public List<Position> chunkPoss;
    @EntityId
    public int mainInventoryId;

    public PlayerConnexionComponent set(Channel channel) {
        this.channel = channel;
        chunkPoss = new ArrayList<>();
        return this;
    }
}
