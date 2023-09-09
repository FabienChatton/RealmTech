package ch.realmtechServer.netty;

import com.badlogic.gdx.math.Vector2;
import io.netty.channel.Channel;

public class RealmtechPacket {
    public Channel newPlayerConnection;
    public Channel removePlayerConnection;
    public Vector2 setPlayerPosition;

    public static RealmtechPacketBuilder builder() {
        return new RealmtechPacketBuilder();
    }

    public static class RealmtechPacketBuilder {
        private RealmtechPacket packet;

        public RealmtechPacketBuilder() {
            this.packet = new RealmtechPacket();
        }

        public RealmtechPacketBuilder setNewPlayerConnection(Channel incomingPlayerChanel) {
            packet.newPlayerConnection = incomingPlayerChanel;
            return this;
        }

        public RealmtechPacketBuilder setRemovePlayerConnection(Channel removePlayerChanel) {
            packet.removePlayerConnection = removePlayerChanel;
            return this;
        }

        public RealmtechPacketBuilder setPlayerPosition(float x, float y) {
            packet.setPlayerPosition = new Vector2(x, y);
            return this;
        }
    }
}
