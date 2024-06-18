package ch.realmtech.server.ecs.component;

import ch.realmtech.server.divers.Position;
import com.artemis.Component;
import com.artemis.utils.Bag;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerConnexionComponent extends Component {
    public Channel channel;
    public int[] ancienChunkPos = null;
    public List<Position> chunkPoss;
    private String username = "unknown";
    public final Bag<UUID> entitySubscription;

    public PlayerConnexionComponent() {
        entitySubscription = new Bag<>();
    }

    public PlayerConnexionComponent set(Channel channel) {
        this.channel = channel;
        chunkPoss = new ArrayList<>();
        return this;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
