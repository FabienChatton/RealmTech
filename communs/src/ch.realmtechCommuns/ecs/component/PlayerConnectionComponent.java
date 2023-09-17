package ch.realmtechCommuns.ecs.component;

import com.artemis.Component;
import io.netty.channel.Channel;

import java.util.UUID;

public class PlayerConnectionComponent extends Component {
    public Channel channel;
    public UUID uuid;

    public PlayerConnectionComponent set(Channel channel, UUID uuid) {
        this.channel = channel;
        this.uuid = uuid;
        return this;
    }
}
