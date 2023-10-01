package ch.realmtechServer.ecs.component;

import com.artemis.Component;
import com.artemis.annotations.EntityId;
import io.netty.channel.Channel;

import java.util.UUID;

public class PlayerConnexionComponent extends Component {
    public Channel channel;
    public UUID uuid;
    public int[] ancienChunkPos = null;
    @EntityId
    public int[] infChunks;

    public PlayerConnexionComponent set(Channel channel, UUID uuid) {
        this.channel = channel;
        this.uuid = uuid;
        infChunks = new int[0];
        return this;
    }
}
