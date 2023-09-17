package ch.realmtechCommuns.ecs.component;

import com.artemis.Component;
import io.netty.channel.Channel;

public class PlayerConnectionComponent extends Component {
    public Channel channel;

    public PlayerConnectionComponent set(Channel channel) {
        this.channel = channel;
        return this;
    }
}
