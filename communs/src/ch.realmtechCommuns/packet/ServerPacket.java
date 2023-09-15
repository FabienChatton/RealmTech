package ch.realmtechCommuns.packet;

import com.artemis.World;

public interface ServerPacket extends Packet {
    void executeOnServer(World world);
}
