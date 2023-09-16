package ch.realmtechCommuns.packet.clientPacket;

import ch.realmtechCommuns.packet.ClientPacket;
import com.artemis.World;
import io.netty.buffer.ByteBuf;

public class ConnectionJoueurReussitPacket implements ClientPacket {
    public ConnectionJoueurReussitPacket(ByteBuf byteBuf) {

    }

    public ConnectionJoueurReussitPacket() {
    }

    @Override
    public void executeOnClient(World world) {
        System.out.println("fumier");
    }

    @Override
    public void write(ByteBuf byteBuf) {

    }
}
