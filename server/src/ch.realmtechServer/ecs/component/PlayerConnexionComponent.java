package ch.realmtechServer.ecs.component;

import ch.realmtechServer.divers.Position;
import com.artemis.Component;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerConnexionComponent extends Component {
    public Channel channel;
    public UUID uuid;
    public UUID mainInventoryUUID;
    public int[] ancienChunkPos = null;
    public List<Position> chunkPoss;

    public PlayerConnexionComponent set(Channel channel, UUID uuid, UUID mainInventoryUUID) {
        this.channel = channel;
        this.uuid = uuid;
        this.mainInventoryUUID = mainInventoryUUID;
        chunkPoss = new ArrayList<>();
        return this;
    }
}
