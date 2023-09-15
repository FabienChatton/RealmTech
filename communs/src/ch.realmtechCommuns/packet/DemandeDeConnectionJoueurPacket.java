package ch.realmtechCommuns.packet;

import com.artemis.World;
import com.artemis.managers.PlayerManager;
import io.netty.buffer.ByteBuf;

public class DemandeDeConnectionJoueurPacket implements ServerPacket {

    public DemandeDeConnectionJoueurPacket(ByteBuf byteBuf) {

    }

    @Override
    public void write(ByteBuf byteBuf) {

    }

    @Override
    public void executeOnServer(World world) {

    }
}
